package net.sophomatics.markov_predictor;

import java.util.Set;

/**
 * Interface for a Markov predictor
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public interface MarkovPredictor<Condition, Consequence> {
    int getFrequency(Condition cause, Consequence effect);

    int getMaxFrequency(Condition cause);

    int getMass(Condition cause);

    Set<Consequence> getAllEffects();

    Set<Condition> getAllCauses();

    float getProbability(Condition cause, Consequence effect);

    void store(Condition cause, Consequence effect);

    Consequence getEffect(Condition cause);

    float getMatch(MarkovPredictor<Condition, Consequence> other);

    void add(MarkovPredictor<Condition, Consequence> other);

    String print();

    int hashCode();

    boolean equals(Object other);

    void clear();

    int getId();
}
