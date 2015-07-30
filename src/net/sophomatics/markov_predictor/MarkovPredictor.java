package net.sophomatics.markov_predictor;

import java.util.Set;

/**
 * Created by mark on 12.07.15.
 */
public interface MarkovPredictor<Condition, Consequence> {
    int getFrequency(Condition cause, Consequence effect);

    float getProbability(Condition cause, Consequence effect);

    void store(Condition cause, Consequence effect);

    Consequence getConsequence(Condition cause);

    float getSimilarity(MarkovPredictor<Condition, Consequence> other);

    void add(MarkovPredictor<Condition, Consequence> other);

    String print();

    boolean containsCondition(Condition cause);

    int hashCode();

    boolean equals(Object other);

    void clear();

    int getMass(Condition cause);

    Set<Consequence> getEffects(Condition cause);

    boolean isKnown(Condition cause);

    int getId();
}
