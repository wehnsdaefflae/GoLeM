package net.sophomatics.agents;

import net.sophomatics.util.Tuple;

import java.util.List;

/**
 * Created by wernsdorfer on 23.08.2015.
 */
public interface Agent<Sensor, Motor> {
    List<Integer> getTrace();

    List<Integer> getStructure();

    @Override
    String toString();

    Motor interact(Sensor s, double reward);

    Sensor predict(Tuple<Sensor, Motor> cause);
}
