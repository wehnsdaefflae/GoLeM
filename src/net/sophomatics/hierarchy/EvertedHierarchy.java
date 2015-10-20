package net.sophomatics.hierarchy;

import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcessFactory;
import net.sophomatics.util.Tuple;

import java.util.*;

/**
 * Created by wernsdorfer on 22.08.2015.
 */
public class EvertedHierarchy<Sensor, Motor> {
    private EvertedHierarchy<Integer, Tuple<Sensor, Motor>> parent;
    private StochasticProcess<Tuple<Integer, Tuple<Sensor, Motor>>, Integer> thisModel;
    private StochasticProcess<Tuple<Integer, Tuple<Sensor, Motor>>, Integer> thisObs;
    private StochasticProcessFactory<Tuple<Sensor, Motor>, Sensor> mFak;
    private double threshold;
    private int nextTypeId;
    private Tuple<Integer, Tuple<Sensor, Motor>> lastCause;
    private int level;

    public EvertedHierarchy(double threshold) {
        this(threshold, 0);
    }

    private EvertedHierarchy(double threshold, int level) {
        this.parent = null;
        this.lastCause = null;
        this.thisModel = null;
        this.nextTypeId = -1;
        this.thisObs = new MatrixStochasticProcess<>(-1);
        this.mFak = new StochasticProcessFactory<>();
        this.threshold = threshold;
        this.level = level;
    }

    public List<Integer> getStructure() {
        List<Integer> s = new ArrayList<>();
        EvertedHierarchy p = this;
        s.add(p.mFak.size());
        while (p.parent != null) {
            p = p.parent;
            s.add(p.mFak.size());
        }
        return Collections.unmodifiableList(s);
    }

    public List<Integer> getTrace() {
        List<Integer> s = new ArrayList<>();
        EvertedHierarchy p = this;
        s.add(p.nextTypeId);
        while (p.parent != null) {
            p = p.parent;
            s.add(p.nextTypeId);
        }
        return Collections.unmodifiableList(s);

    }

    private double getMatch(StochasticProcess<Tuple<Sensor, Motor>, Sensor> token, StochasticProcess<Tuple<Sensor, Motor>, Sensor> type) {
        return type.getSimilarity(token);
    }

    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> getType(StochasticProcess<Tuple<Sensor, Motor>, Sensor> token) {
        double thisMatch, maxMatch = -1d;
        StochasticProcess<Tuple<Sensor, Motor>, Sensor> bestModel = null;
        for (StochasticProcess<Tuple<Sensor, Motor>, Sensor> eachModel : this.mFak) { // search only in parent model
            thisMatch = this.getMatch(token, eachModel);
            if (maxMatch < thisMatch) {
                bestModel = eachModel;
                maxMatch = thisMatch;
            }
        }
        if (maxMatch >= this.threshold) {
            return bestModel;
        }
        return this.mFak.newInstance();
    }

    private boolean isBreakdown(int typeId) {
        return (this.thisObs.getFrequency(this.lastCause, typeId) < this.thisObs.getMaxFrequency(this.lastCause));
    }

    private int predict(Tuple<Integer, Tuple<Sensor, Motor>> cause) {
        Set<Integer> allEffects = new HashSet<>(this.thisObs.getAllEffects());
        if (this.thisModel != null) {
            allEffects.addAll(this.thisModel.getAllEffects());
        }

        if (allEffects.isEmpty()) {
            return cause.a;
        }

        int bestEffect = -1;
        int thisValue, bestValue = -1;

        for (int eachEffect : allEffects) {
            thisValue = thisObs.getFrequency(cause, eachEffect);
            if (this.thisModel != null) {
                thisValue += this.thisModel.getFrequency(cause, eachEffect);
            }
            if (bestValue < thisValue) {
                bestValue = thisValue;
                bestEffect = eachEffect;
            }
        }

        return bestEffect;
    }

    public StochasticProcess<Tuple<Sensor, Motor>, Sensor> getNextContext(StochasticProcess<Tuple<Sensor, Motor>, Sensor> sensor, Tuple<Sensor, Motor> motor) {
        StochasticProcess<Tuple<Sensor, Motor>, Sensor> sensorType, expectedType;
        int typeId;

        if (this.lastCause != null) {
            expectedType = this.mFak.get(this.nextTypeId);

            if (this.threshold < this.getMatch(sensor, expectedType)) {
                sensorType = expectedType;
            } else {
                sensorType = this.getType(sensor);
            }

            typeId = sensorType.getId();
            if (isBreakdown(typeId)) {
                Tuple<Integer, Tuple<Sensor, Motor>> action = new Tuple<>(typeId, motor);
                if (this.parent == null) {
                    this.parent = new EvertedHierarchy<>(this.threshold, this.level + 1);
                }
                this.thisModel = this.parent.getNextContext(this.thisObs, action);
            }
            this.thisObs.store(this.lastCause, typeId);

        } else {
            sensorType = this.getType(sensor);
            typeId = sensorType.getId();
        }

        sensorType.add(sensor);
        sensor.clear();

        this.lastCause = new Tuple<>(typeId, motor);
        this.nextTypeId = this.predict(this.lastCause);
        return this.mFak.get(this.nextTypeId);
    }
}
