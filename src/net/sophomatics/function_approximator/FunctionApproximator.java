package net.sophomatics.function_approximator;

/**
 * Created by wernsdorfer on 10.08.2015.
 */
public interface FunctionApproximator<Condition> {
    void approximate(Condition cause, float value);
    float getValue(Condition cause);
}
