package net.sophomatics.agents;

import net.sophomatics.hierarchy.Hierarchy;
import net.sophomatics.stochastic_process.StochasticProcess;
import net.sophomatics.util.Tuple;

import java.util.*;

/**
 * Agent class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-10
 */
public class HierarchicalAgent<Sensor, Motor> implements Agent<Sensor, Motor> {
    private final Random r;
    private final Hierarchy<Sensor, Motor> h;
    private final Set<Motor> actions;
    private Sensor lastSensor;
    private Motor lastMotor;
    private float epsilon;
    private int noInteractions;

    public HierarchicalAgent(float threshold, Set<Motor> actions) {
        this.r = new Random(3771);
        this.h = new Hierarchy<>(threshold, this.r);
        this.actions = actions;
        this.epsilon = .1f;
        this.noInteractions = 0;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.hashCode();
    }

    @Override
    public List<Integer> getTrace() {
        List<StochasticProcess> lMp = h.getTrace();
        List<Integer> lInt = new ArrayList<>(lMp.size());
        for (StochasticProcess eachMp : lMp) {
            lInt.add(eachMp.getId());
        }
        return lInt;
    }

    @Override
    public List<Integer> getStructure() {
        return this.h.getStructure();
    }

    private Motor randomMotor() {
        Iterator<Motor> it = actions.iterator();
        Motor eachMotor = null;
        for (int p = r.nextInt(actions.size()); p-- >= 0; eachMotor = it.next()) ;
        return eachMotor;
    }

    private Motor act(Sensor s) {
        if (r.nextFloat() < this.epsilon || this.noInteractions < 1) {
            return randomMotor();
        }
        return h.act(s);
    }

    @Override
    public Motor interact(Sensor s, double reward) {
        if (lastSensor != null && lastMotor != null) {
            h.perceive(lastSensor, lastMotor, s);
        }
        lastMotor = this.act(s);
        lastSensor = s;

        this.noInteractions++;
        return lastMotor;
    }

    @Override
    public Sensor predict(Tuple<Sensor, Motor> cause) {
        return this.h.predict(cause);
    }
}
