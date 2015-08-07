package net.sophomatics.hierarchy;


import net.sophomatics.markov_predictor.MarkovPredictor;
import net.sophomatics.markov_predictor.MarkovPredictorFactory;
import net.sophomatics.util.Tuple;

import java.util.*;
import java.util.logging.Logger;

/**
 * Recursive hierarchy class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 *
 */
public class Hierarchy<Sensor, Motor> {
    private final static Logger logger = Logger.getLogger(Hierarchy.class.getSimpleName());
    private final MarkovPredictorFactory<Tuple<Sensor, Motor>, Sensor> mFak;
    private final Set<MarkovPredictor<Tuple<Sensor, Motor>, Sensor>> models;
    private final MarkovPredictor<Tuple<Sensor, Motor>, Sensor> tempModel;

    private MarkovPredictor<Tuple<Sensor, Motor>, Sensor> currentModel;
    private MarkovPredictor<Tuple<Sensor, Motor>, Sensor> lastModel;

    private Hierarchy<Integer, Tuple<Sensor, Motor>> parent;

    private Tuple<Sensor, Motor> lastCause;
    private Tuple<Sensor, Motor> nextCause;
    private final float threshold;
    public final int level;

    public Hierarchy(int level, float threshold) {
        this.level = level;
        this.parent = null;
        this.threshold = threshold;
        this.mFak = new MarkovPredictorFactory<>();
        this.lastModel = null;
        this.lastCause = null;
        this.tempModel = this.mFak.newInstance();
        this.currentModel = this.mFak.newInstance();
        this.models = new HashSet<>();
        this.models.add(this.currentModel);
    }

    public Hierarchy(float threshold) {
        this(0, threshold);
    }

    public List<Integer> getStructure() {
        List<Integer> structure = new ArrayList<>();
        for (Hierarchy m = this; m != null; m = m.parent) {
            structure.add(m.models.size());
        }
        return structure;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();

        if (this.parent != null) {
            sb.append(this.parent.print());
            sb.append("\n");
        }

        List<Integer> structure = this.getStructure();
        sb.append(String.format("Level %s, %s\n", this.level, Arrays.toString(structure.toArray())));
        for (MarkovPredictor<Tuple<Sensor, Motor>, Sensor> eachModel : this.mFak) {
            sb.append(eachModel.print());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<MarkovPredictor> getTrace() {
        List<MarkovPredictor> trace = new ArrayList<>();
        for (Hierarchy m = this; m != null; m = m.parent) {
            trace.add(m.currentModel);
        }
        return trace;
    }

    private MarkovPredictor<Tuple<Sensor, Motor>, Sensor> findModel() {
        MarkovPredictor<Tuple<Sensor, Motor>, Sensor> bestModel;

        float thisValue, bestValue = -1f;
        List<MarkovPredictor<Tuple<Sensor, Motor>, Sensor>> bestModelList = new ArrayList<>();

        for (MarkovPredictor<Tuple<Sensor, Motor>, Sensor> eachModel : this.models) {
            thisValue = eachModel.getMatch(this.tempModel);

            if (bestValue < thisValue) {
                bestModelList.clear();
                bestModelList.add(eachModel);
                bestValue = thisValue;

            } else if (bestValue == thisValue) {
                bestModelList.add(eachModel);
            }
        }

        if (bestValue >= this.threshold) {
            bestModel = bestModelList.get(0);

        } else {
            bestModel = this.mFak.newInstance();
            this.models.add(bestModel);
            if (this.parent == null) {
                this.parent = new Hierarchy<>(this.level + 1, this.threshold);
            }
        }

        return bestModel;
    }

    private boolean isBreakdown(Tuple<Sensor, Motor> cause, Sensor effect) {
        int bestFreq = this.tempModel.getMaxFrequency(cause);
        int thisFreq = this.tempModel.getFrequency(cause, effect);
        bestFreq += this.currentModel.getMaxFrequency(cause);
        thisFreq += this.currentModel.getFrequency(cause, effect);
        return thisFreq < bestFreq;
    }

    public void perceive(Sensor s0, Motor m0, Sensor s1) {
        Tuple<Sensor, Motor> cause = new Tuple<>(s0, m0);

        if (this.isBreakdown(cause, s1)) {
            MarkovPredictor<Tuple<Sensor, Motor>, Sensor> bestModel;

            if (this.parent == null) {
                bestModel = this.currentModel;

            } else {
                int bestId = this.parent.predict(currentModel.getId(), cause);
                bestModel = this.mFak.get(bestId);
            }

            float sim = bestModel.getMatch(this.tempModel);
            if (sim < this.threshold) {
                bestModel = this.findModel();
            }

            bestModel.add(this.tempModel);
            this.tempModel.clear();

            if (this.parent != null) {
                if (this.lastModel != null && this.lastCause != null) {
                    this.parent.perceive(this.lastModel.getId(), this.lastCause, bestModel.getId());
                    int newId = this.parent.predict(bestModel.getId(), cause);
                    this.nextCause = this.parent.act(newId);
                    this.currentModel = this.mFak.get(newId);
                }
            }
            this.lastModel = bestModel;
            this.lastCause = cause;
        }

        this.tempModel.store(cause, s1);
    }

    public Sensor predict(Sensor s, Motor m) {
        Set<Sensor> allCons = this.currentModel.getAllConsequences();
        allCons.addAll(this.tempModel.getAllConsequences());

        if (allCons.size() < 1) {
            return s;
        }

        Tuple<Sensor, Motor> cause = new Tuple<>(s, m);

        Sensor bestSensor = null;
        double thisValue, maxValue = -1d;

        for (Sensor s1 : allCons) {
            thisValue = this.currentModel.getFrequency(cause, s1) + this.tempModel.getFrequency(cause, s1);
            if (maxValue < thisValue) {
                bestSensor = s1;
                maxValue = thisValue;
            } else if (maxValue == thisValue) {
                bestSensor = s1;
            }
        }

        return bestSensor;
    }

    private Motor act(Sensor s) {
        if (this.nextCause != null && s.equals(this.nextCause.a)) {
            // TODO: decide whether long term goals or short term goals, store cumulative expected discounted reward?
            return this.nextCause.b;
        }
        return null;
    }
}
