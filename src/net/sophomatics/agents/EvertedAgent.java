package net.sophomatics.agents;

import net.sophomatics.hierarchy.EvertedHierarchy;
import net.sophomatics.stochastic_process.MatrixStochasticProcess;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.util.Tuple;

import java.util.*;

/**
 * Created by wernsdorfer on 22.08.2015.
 */
public class EvertedAgent<Sensor, Motor> implements Agent<Sensor, Motor> {
    private final double threshold;
    private EvertedHierarchy<Sensor, Motor> h;
    private StochasticProcess<Tuple<Sensor, Motor>, Sensor> context;
    private final StochasticProcess<Tuple<Sensor, Motor>, Sensor> observation;
    private Tuple<Sensor, Motor> lastCause;
    private final Set<Motor> actions;
    private final Random r;

    public EvertedAgent(double threshold, Set<Motor> actions) {
        this.threshold = threshold;
        this.h = null;
        this.observation = new MatrixStochasticProcess<>(-1);
        this.context = null;
        this.lastCause = null;
        this.actions = actions;
        this.r = new Random(3771);
    }

    private Motor act(Sensor s) {
        Iterator<Motor> it = this.actions.iterator();
        Motor eachMotor = null;
        for (int p = this.r.nextInt(this.actions.size()); p-- >= 0; eachMotor = it.next()) ;
        return eachMotor;
    }

    private boolean isBreakdown(Tuple<Sensor, Motor> cause, Sensor effect) {
        return this.observation.getFrequency(cause, effect) < this.observation.getMaxFrequency(cause);
    }

    @Override
    public Motor interact(Sensor s, double reward) {
        if (this.lastCause != null) {
            if (this.isBreakdown(this.lastCause, s)) {
                if (this.h == null) {
                    this.h = new EvertedHierarchy<>(threshold);
                }
                this.context = this.h.getNextContext(this.observation, this.lastCause);
            }
            this.observation.store(this.lastCause, s);
        }
        Motor m = this.act(s);
        this.lastCause = new Tuple<>(s, m);
        return m;
    }

    @Override
    public List<Integer> getTrace() {
        return this.h.getTrace();
    }

    @Override
    public List<Integer> getStructure() {
        if (this.h == null) {
            return new ArrayList<>();
        }
        return this.h.getStructure();
    }

    @Override
    public Sensor predict(Tuple<Sensor, Motor> cause) {
        if (this.context == null) {
            return cause.a;
        }
        return this.context.getEffect(cause);
    }
}
