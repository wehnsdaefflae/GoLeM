package net.sophomatics.function_approximator;

import net.sophomatics.util.Identifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wernsdorfer on 10.08.2015.
 */
public class DiscreteApproximator<Condition> extends Identifiable implements FunctionApproximator<Condition> {
    private final Map<Condition, Float> table;
    private final float alpha;

    public DiscreteApproximator(int id) {
        super(id);
        this.table = new HashMap<>();
        this.alpha = .1f;
    }

    @Override
    public void approximate(Condition cause, float newValue) {
        float oldValue = this.getValue(cause);
        this.table.put(cause, oldValue + this.alpha * (newValue - oldValue));
    }

    @Override
    public float getValue(Condition cause) {
        Float v = this.table.get(cause);
        if (v == null) {
            return 0;
        }
        return v;
    }
}
