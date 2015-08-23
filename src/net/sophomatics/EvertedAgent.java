package net.sophomatics;

import net.sophomatics.hierarchy.EvertedHierarchy;
import net.sophomatics.hierarchy.Hierarchy;
import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.util.Tuple;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by wernsdorfer on 22.08.2015.
 */
public class EvertedAgent<Sensor, Motor> {
    private EvertedHierarchy<Sensor, Motor> h;
    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> model;
    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> obsModel;
    private Sensor lastSensor;
    private Set<Motor> actions;
    private Random r;

    public EvertedAgent(Set<Motor> actions) {
        this.h = null;
        this.obsModel = new MatrixStochasticProcess<>(-1);
        this.model = null;
        this.lastSensor = null;
        this.actions = actions;
        this.r = new Random(198553);
    }

    private Motor act(Sensor s) {
        Iterator<Motor> it = this.actions.iterator();
        Motor eachMotor = null;
        for (int p = this.r.nextInt(this.actions.size()); p-- >= 0; eachMotor = it.next()) ;
        return eachMotor;
    }

    public Motor interact(Motor m, Sensor s) {
        if (this.lastSensor != null) {
            Tuple<Sensor, Motor> cause = new Tuple<>(this.lastSensor, m);
            if (this.model.getFrequency(cause, s) < model.getMaxFrequency(cause)) {
                if (this.h == null) {
                    this.h = new EvertedHierarchy<>(1d);
                }
                this.model = this.h.getNextModel(this.obsModel, cause);
                this.obsModel.clear();
            }
        }

        this.lastSensor = s;
        return this.act(s);
    }

    public Sensor predict(Motor m) {
        return this.model.getEffect(new Tuple<>(this.lastSensor, m));
    }
}
