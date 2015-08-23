package net.sophomatics.hierarchy;


import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcessFactory;
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
    public final int level;
    private final StochasticProcessFactory<Tuple<Sensor, Motor>, Sensor> mFak;
    private final StochasticProcess<Tuple<Sensor, Motor>, Sensor> tempModel;
    private final float threshold;
    private final Random r;
    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> currentModel, lastModel;
    private Hierarchy<Integer, Tuple<Sensor, Motor>> parent;
    private Tuple<Sensor, Motor> lastCause, nextCause;
    private Map<Sensor, Double> stateProbability;

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
        this.stateProbability = new HashMap<>();
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
            thisValue = eachModel.getSimilarity(this.tempModel);
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

    private float getJointProbability(Tuple<Sensor, Motor> cause, Sensor effect) {
        int freq = this.tempModel.getFrequency(cause, effect);
        int mass = this.tempModel.getMass(cause);
        if (false && this.currentModel != null) {
            freq += this.currentModel.getFrequency(cause, effect);
            mass += this.currentModel.getMass(cause);
        }

        return mass < 1 ? 0f : (float) freq / mass;
    }

    private boolean isBreakdown(Tuple<Sensor, Motor> cause, Sensor effect) {
        int thisFreq = this.tempModel.getFrequency(cause, effect);
        int bestFreq = this.tempModel.getMaxFrequency(cause);
        if (false && this.currentModel != null) {
            thisFreq += this.currentModel.getFrequency(cause, effect);
            bestFreq += this.currentModel.getMaxFrequency(cause);
        }
        return thisFreq < bestFreq;
    }

    public void perceive(Sensor s0, Motor m0, Sensor s1) {
        Tuple<Sensor, Motor> cause = new Tuple<>(s0, m0);

        if (this.isBreakdown(cause, s1)) {
            StochasticProcess<Tuple<Sensor, Motor>, Sensor> thisModel;

            if (this.parent == null) {
                this.parent = new Hierarchy<>(this.level + 1, this.threshold, this.r);
                this.currentModel = this.mFak.newInstance();
                thisModel = this.currentModel;

            } else {
                int bestId = this.parent.predict(this.currentModel.getId(), cause);
                thisModel = this.mFak.get(bestId);

                float sim = thisModel.getSimilarity(this.tempModel);
                if (sim < this.threshold) {
                    thisModel = this.findModel();
                }
            }
            thisModel.add(this.tempModel);

            if (this.lastModel != null && this.lastCause != null) {
                this.parent.perceive(this.lastModel.getId(), this.lastCause, thisModel.getId());
                int nextId = this.parent.predict(thisModel.getId(), cause);
                this.currentModel = this.mFak.get(nextId);
                this.nextCause = this.parent.act(nextId);
            }

            this.tempModel.clear();
            this.lastModel = thisModel;
            this.lastCause = cause;
        }

        this.tempModel.store(cause, s1);
    }

    public String printBeliefDistribution() {
        return this.stateProbability.toString();
    }

    private float compare(Sensor s0, Sensor s1) {
        if (this.level < 1) {
            return s0.equals(s1) ? 1f : 0f;
        }
        int id0 = (Integer) s0;
        int id1 = (Integer) s1;
        return this.mFak.get(id0).getSimilarity(this.mFak.get(id1));
    }

    private void sensorUpdateStateProbability(Sensor observation) {
        Sensor state;
        double newValue, sum = 0d;

        // update
        for (Map.Entry<Sensor, Double> entry : this.stateProbability.entrySet()) {
            state = entry.getKey();
            newValue = entry.getValue() * this.compare(state, observation); // TODO: observation == state!
            if (Double.isNaN(newValue)) {
                throw new IllegalArgumentException();
            }
            entry.setValue(newValue);
            sum += newValue;
        }

        // resample
        for (Map.Entry<Sensor, Double> entry : this.stateProbability.entrySet()) {
            if (sum == 0d) {
                newValue = 0d;
            } else {
                newValue = entry.getValue() / sum;
            }
            entry.setValue(newValue);
        }
    }

    private void motorUpdateStateProbability(Motor action) {
        Map<Sensor, Double> posterior = new HashMap<>(this.stateProbability.size());
        double v;
        float probability;
        Sensor fromState, effect;
        Tuple<Sensor, Motor> abstractCause;

        // for all abstract states as effects...
        for (Map.Entry<Sensor, Double> entry : this.stateProbability.entrySet()) {
            v = 0d;
            effect = entry.getKey();

            // ... check all abstract states and the current abstract action as cause
            for (Map.Entry<Sensor, Double> subEntry : this.stateProbability.entrySet()) {
                fromState = subEntry.getKey();
                abstractCause = new Tuple<>(fromState, action);
                probability = this.getJointProbability(abstractCause, effect);
                v += probability * this.stateProbability.get(fromState);

                if (Double.isNaN(v)) {
                    throw new IllegalArgumentException();
                }
            }
            // update the belief distribution accordingly
            posterior.put(effect, v);
        }
        this.stateProbability = posterior;
    }

    public Sensor predict(Sensor s, Motor m) {
        Set<Sensor> allCons = new HashSet<>(this.tempModel.getAllEffects());
        if (this.currentModel != null) {
            allCons.addAll(this.currentModel.getAllEffects());
        }

        if (allCons.size() < 1) {
            return s;
        }

        Tuple<Sensor, Motor> cause = new Tuple<>(s, m);

        Sensor bestSensor = null;
        double thisValue, maxValue = -1d;

        for (Sensor s1 : allCons) {
            thisValue = this.tempModel.getFrequency(cause, s1);
            if (this.currentModel != null) {
                thisValue += this.currentModel.getFrequency(cause, s1);
            }
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
