package net.sophomatics;

import net.sophomatics.hierarchy.Hierarchy;
import net.sophomatics.stochastic_process.StochasticProcess;

import java.util.*;

/**
 * Agent class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-10
 */
public class Agent<Sensor, Motor> {
    private final Random r;
    public final Hierarchy<Sensor, Motor> h;
    private final Set<Motor> actions;
    private Sensor lastSensor;
    private Motor lastMotor;
    private float epsilon;
    private int noInteractions;

    public Agent(float threshold, Set<Motor> actions) {
        this.r = new Random(3771);
        this.h = new Hierarchy<>(threshold, this.r);
        this.actions = actions;
        this.epsilon = .1f;
        this.noInteractions = 0;
    }

    @Override
    public String toString() {
        return this.h.print();
    }

    public String getStructureString() {
        return Arrays.toString(h.getStructure().toArray());
    }

    public List<Integer> getTrace() {
        List<StochasticProcess> lMp = h.getTrace();
        List<Integer> lInt = new ArrayList<>(lMp.size());
        for (StochasticProcess eachMp : lMp) {
            lInt.add(eachMp.getId());
        }
        return lInt;
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

    public Sensor predict(Sensor s, Motor m) {
        return h.predict(s, m);
    }

    public Motor interact(Sensor s, float reward) {
        if (lastSensor != null && lastMotor != null) {
            h.perceive(lastSensor, lastMotor, s);
        }
        lastMotor = this.act(s);
        lastSensor = s;

        this.noInteractions++;
        return lastMotor;
    }
}
