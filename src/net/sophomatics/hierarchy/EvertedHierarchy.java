package net.sophomatics.hierarchy;

import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcessFactory;
import net.sophomatics.util.Tuple;

/**
 * Created by wernsdorfer on 22.08.2015.
 */
public class EvertedHierarchy<Sensor, Motor> {
    private EvertedHierarchy<Integer, Tuple<Sensor, Motor>> parent;
    private StochasticProcess<Tuple<Integer, Tuple<Sensor, Motor>>, Integer> thisModel;
    private StochasticProcess<Tuple<Integer, Tuple<Sensor, Motor>>, Integer> thisObs;
    private StochasticProcessFactory<Tuple<Sensor, Motor>, Sensor> mFak;
    private double threshold;
    private Tuple<Integer, Tuple<Sensor, Motor>> lastCause;
    private int level;

    public EvertedHierarchy(double threshold) {
        this(threshold, 0);
    }

    private EvertedHierarchy(double threshold, int level) {
        this.parent = null;
        this.lastCause = null;
        this.thisModel = null;
        this.thisObs = new MatrixStochasticProcess<>(-1);
        this.mFak = new StochasticProcessFactory<>();
        this.threshold = threshold;
        this.level = level;
    }

    private double getMatch(StochasticProcess<Tuple<Sensor, Motor>, Sensor> token, StochasticProcess<Tuple<Sensor, Motor>, Sensor> type) {
        return type.getSimilarity(token);
    }

    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> getType(StochasticProcess<Tuple<Sensor, Motor>, Sensor> token) {
        double thisMatch, maxMatch = -1d;
        StochasticProcess<Tuple<Sensor, Motor>, Sensor> bestModel = null;
        for (StochasticProcess<Tuple<Sensor, Motor>, Sensor> eachModel : this.mFak) {
            thisMatch = this.getMatch(token, eachModel);
            if (maxMatch < thisMatch) {
                bestModel = eachModel;
                maxMatch = thisMatch;
            }
        }
        if (maxMatch < this.threshold) {
            bestModel = this.mFak.newInstance();
        }
        return bestModel;
    }

    public StochasticProcess<Tuple<Sensor, Motor>, Sensor> getNextModel(StochasticProcess<Tuple<Sensor, Motor>, Sensor> sensor, Tuple<Sensor, Motor> motor) {
        StochasticProcess<Tuple<Sensor, Motor>, Sensor> sensorType, expectedType;
        int typeId, expectedTypeId;

        if (this.lastCause != null) {
            expectedTypeId = this.thisModel.getEffect(this.lastCause);
            expectedType = this.mFak.get(expectedTypeId);

            if (this.threshold < this.getMatch(sensor, expectedType)) {
                sensorType = expectedType;
            } else {
                sensorType = this.getType(sensor);
            }

            typeId = sensorType.getId();
            if (this.thisModel.getFrequency(this.lastCause, typeId) < this.thisModel.getMaxFrequency(this.lastCause)) {
                Tuple<Integer, Tuple<Sensor, Motor>> action = new Tuple<>(typeId, motor);
                if (this.parent == null) {
                    this.parent = new EvertedHierarchy<>(this.threshold, this.level + 1);
                }
                this.thisModel = this.parent.getNextModel(this.thisObs, action);
                this.thisModel.add(this.thisObs);
                this.thisObs.clear();
            }
            this.thisObs.store(lastCause, typeId);

        } else {
            sensorType = this.getType(sensor);
            typeId = sensorType.getId();
        }

        this.lastCause = new Tuple<>(typeId, motor);
        int nextTypeId = this.thisModel.getEffect(this.lastCause);
        return this.mFak.get(nextTypeId);
    }
}
