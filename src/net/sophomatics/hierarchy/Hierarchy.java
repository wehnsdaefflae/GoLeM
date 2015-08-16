package net.sophomatics.hierarchy;


import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcessFactory;
import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.util.Tuple;

import java.util.*;
import java.util.logging.Logger;

/**
 * Recursive hierarchy class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public class Hierarchy<Sensor, Motor> {
    private final static Logger logger = Logger.getLogger(Hierarchy.class.getSimpleName());
    private final StochasticProcessFactory<Tuple<Sensor, Motor>, Sensor> mFak;
    private final StochasticProcess<Tuple<Sensor, Motor>, Sensor> tempModel;

    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> currentModel;
    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> lastModel;

    private Hierarchy<Integer, Tuple<Sensor, Motor>> parent;

    private Tuple<Sensor, Motor> lastCause;
    private Tuple<Sensor, Motor> nextCause;
    private final float threshold;
    public final int level;

    private final Random r;

    private Hierarchy(int level, float threshold, Random r) {
        this.level = level;
        this.parent = null;
        this.threshold = threshold;
        this.mFak = new StochasticProcessFactory<>();
        this.currentModel = null;
        this.lastModel = null;
        this.lastCause = null;
        this.tempModel = new MatrixStochasticProcess<>(-1);
        this.r = r;
    }

    public Hierarchy(float threshold, Random r) {
        this(0, threshold, r);
    }

    public List<Integer> getStructure() {
        List<Integer> structure = new ArrayList<>();
        for (Hierarchy m = this; m != null; m = m.parent) {
            structure.add(m.mFak.getProducts().size());
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
        sb.append(tempModel.print());
        sb.append("\n");
        for (StochasticProcess<Tuple<Sensor, Motor>, Sensor> eachModel : this.mFak) {
            sb.append(eachModel.print());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<StochasticProcess> getTrace() {
        List<StochasticProcess> trace = new ArrayList<>();
        for (Hierarchy m = this; m.currentModel != null; m = m.parent) {
            trace.add(m.currentModel);
        }
        return trace;
    }

    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> findModel() {
        StochasticProcess<Tuple<Sensor, Motor>, Sensor> bestModel = null;

        float thisValue, bestValue = this.threshold;
        for (StochasticProcess<Tuple<Sensor, Motor>, Sensor> eachModel : this.mFak) {
            thisValue = eachModel.getMatch(this.tempModel);
            if (thisValue >= bestValue) {
                bestModel = eachModel;
                bestValue = thisValue;
            }
        }

        if (bestModel == null) {
            bestModel = this.mFak.newInstance();
        }

        return bestModel;
    }

    private boolean isBreakdown(Tuple<Sensor, Motor> cause, Sensor effect) {
        int bestFreq = this.tempModel.getMaxFrequency(cause);
        int thisFreq = this.tempModel.getFrequency(cause, effect);
        if (false && this.currentModel != null) {
            bestFreq += this.currentModel.getMaxFrequency(cause);
            thisFreq += this.currentModel.getFrequency(cause, effect);
        }
        return thisFreq < bestFreq;
    }

    public void perceive(Sensor s0, Motor m0, Sensor s1) {
        Tuple<Sensor, Motor> cause = new Tuple<>(s0, m0);

        if (this.isBreakdown(cause, s1)) {
            StochasticProcess<Tuple<Sensor, Motor>, Sensor> bestModel;

            if (this.parent == null) {
                this.parent = new Hierarchy<>(this.level + 1, this.threshold, this.r);
                this.currentModel = this.mFak.newInstance();
                bestModel = this.currentModel;

            } else {
                int bestId = this.parent.predict(this.currentModel.getId(), cause);
                bestModel = this.mFak.get(bestId);

                float sim = bestModel.getMatch(this.tempModel);
                if (sim < this.threshold) {
                    bestModel = this.findModel();
                }
            }

            bestModel.add(this.tempModel);
            this.tempModel.clear();

            if (this.lastModel != null && this.lastCause != null) {
                this.parent.perceive(this.lastModel.getId(), this.lastCause, bestModel.getId());
                int nextId = this.parent.predict(bestModel.getId(), cause);
                this.currentModel = this.mFak.get(nextId);
                this.nextCause = this.parent.act(nextId);
            }

            this.lastModel = bestModel;
            this.lastCause = cause;
        }

        this.tempModel.store(cause, s1);
    }

    public Sensor predict(Sensor s, Motor m) {
        Set<Sensor> allCons = new HashSet<>();
        if (this.currentModel != null) {
            this.currentModel.getAllEffects();
        }
        allCons.addAll(this.tempModel.getAllEffects());

        if (allCons.size() < 1) {
            return s;
        }

        Tuple<Sensor, Motor> cause = new Tuple<>(s, m);

        Sensor bestSensor = null;
        double thisValue, maxValue = -1d;

        for (Sensor s1 : allCons) {
            thisValue = 0;
            if (this.currentModel != null) {
                this.currentModel.getFrequency(cause, s1);
            }
            thisValue += this.tempModel.getFrequency(cause, s1);
            if (maxValue < thisValue) {
                bestSensor = s1;
                maxValue = thisValue;
            }
        }

        return bestSensor;
    }

    public Motor act(Sensor s) {
        if (this.nextCause != null && s.equals(this.nextCause.a)) {
            // TODO: decide whether long term goals or short term goals, store cumulative expected discounted reward?
            return this.nextCause.b;
        }

        Set<Motor> actions = new HashSet<>();
        for (Tuple<Sensor, Motor> eachCause : this.tempModel.getAllCauses()) {
            actions.add(eachCause.b);
        }

        if (this.currentModel != null) {
            for (Tuple<Sensor, Motor> eachCause : this.currentModel.getAllCauses()) {
                actions.add(eachCause.b);
            }
        }

        Motor m = null;
        Iterator<Motor> it = actions.iterator();
        for (int p = r.nextInt(actions.size()); p-- >= 0; m = it.next()) ;
        return m;
    }
}
