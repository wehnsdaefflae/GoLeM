package net.sophomatics.stochastic_process;

import java.util.Set;

/**
 * Interface for a Markov predictor
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public interface StochasticProcess<Condition, Consequence> {
    int getFrequency(Condition cause, Consequence effect);

    int getMaxFrequency(Condition cause);

    int getMass(Condition cause);

    Set<Consequence> getAllEffects();

    Set<Condition> getAllCauses();

    float getProbability(Condition cause, Consequence effect);

    void store(Condition cause, Consequence effect);

    Consequence getEffect(Condition cause);

    float getSimilarity(StochasticProcess<Condition, Consequence> other);

    void add(StochasticProcess<Condition, Consequence> other);

    String print();

    int hashCode();

    boolean equals(Object other);

    void clear();

    int getId();
}
