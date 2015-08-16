package net.sophomatics.stochastic_process;

import net.sophomatics.stochastic_process.matrix.Matrix;
import net.sophomatics.stochastic_process.matrix.NestedMapMatrix;
import net.sophomatics.util.Identifiable;

import java.util.*;
import java.util.logging.Logger;

/**
 * Implementation of a Markov predictor
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public class MatrixStochasticProcess<Condition, Consequence> extends Identifiable implements StochasticProcess<Condition, Consequence> {
    private final Matrix<Condition, Consequence, Integer> matrix;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public MatrixStochasticProcess(int id) {
        super(id);
        this.matrix = new NestedMapMatrix<>();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof StochasticProcess)) {
            return false;
        }
        MatrixStochasticProcess cast = (MatrixStochasticProcess) other;
        return cast.matrix.equals(this.matrix);
    }

    @Override
    public int hashCode() {
        return 37 * 3 + this.matrix.hashCode();
    }

    @Override
    public void clear() {
        this.matrix.clear();
    }

    public String print() {
        return this.toString() + "\n" + this.matrix.print();
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.getClass().getSimpleName(), this.getId());
    }

    @Override
    public int getFrequency(Condition cause, Consequence effect) {
        Integer f = this.matrix.get(cause, effect);
        if (f == null) {
            return 0;
        }
        return f;
    }

    @Override
    public int getMaxFrequency(Condition cause) {
        Map<Consequence, Integer> subMap = this.matrix.get(cause);
        if (subMap == null) {
            return 0;
        }

        int maxFreq = -1;
        for (int eachFreq : subMap.values()) {
            if (maxFreq < eachFreq) {
                maxFreq = eachFreq;
            }
        }
        return maxFreq;
    }

    @Override
    public int getMass(Condition cause) {
        Map<Consequence, Integer> subMap = this.matrix.get(cause);
        if (subMap == null) {
            return 0;
        }
        int mass = 0;
        for (int eachFreq : subMap.values()) {
            mass += eachFreq;
        }
        return mass;
    }

    @Override
    public Set<Consequence> getAllEffects() {
        Set<Consequence> allCons = new HashSet<>();
        for (Condition eachCondition : this.matrix.keySet()) {
            allCons.addAll(this.matrix.getKeys(eachCondition));
        }
        return allCons;
    }

    @Override
    public Set<Condition> getAllCauses() {
        return new HashSet<>(this.matrix.keySet());
    }

    @Override
    public void store(Condition cause, Consequence effect) {
        this.matrix.put(cause, effect, this.getFrequency(cause, effect) + 1);
    }

    @Override
    public Consequence getEffect(Condition cause) {
        Map<Consequence, Integer> row = this.matrix.get(cause);
        if (row == null) {
            return null;
        }

        List<Consequence> nextList = new ArrayList<>();
        int thisValue, maxValue = -1;
        for (Map.Entry<Consequence, Integer> entry : row.entrySet()) {
            thisValue = entry.getValue();
            if (thisValue == maxValue) {
                nextList.add(entry.getKey());

            } else if (maxValue < thisValue) {
                nextList.clear();
                nextList.add(entry.getKey());
                maxValue = thisValue;
            }
        }

        return nextList.get(0);
    }

    @Override
    public float getMatch(StochasticProcess<Condition, Consequence> other) {
        return this.getCosineSimilarity(other);
        //return this.getLikelihood(other);
        //return this.getVectorDistance(other);
        //return this.getDeviationQuotient(other);
    }

    public float getCosineSimilarity(StochasticProcess<Condition, Consequence> other) {
        MatrixStochasticProcess<Condition, Consequence> cast = (MatrixStochasticProcess<Condition, Consequence>) other;

        Set<Condition> causes = new HashSet<>(this.matrix.keySet());
        causes.addAll(cast.matrix.keySet());

        Set<Consequence> effects = new HashSet<>();

        for (Condition eachCause : causes) {
            effects.addAll(this.matrix.getKeys(eachCause));
            effects.addAll(cast.matrix.getKeys(eachCause));
        }

        int dotProduct = 0;
        int normA = 0;
        int normB = 0;
        int a;
        int b;
        for (Condition eachCause : causes) {
            for (Consequence eachEffect : effects) {
                a = this.getFrequency(eachCause, eachEffect);
                b = cast.getFrequency(eachCause, eachEffect);
                normA += (int) Math.pow(a, 2);
                normB += (int) Math.pow(b, 2);
                dotProduct += a * b;
            }
        }

        if (normA < 1 || normB < 1) {
            return 0f;
        }

        return (float) (dotProduct / Math.sqrt(normA * normB));
    }

    public float getVectorDistance(StochasticProcess<Condition, Consequence> other) {
        MatrixStochasticProcess<Condition, Consequence> cast = (MatrixStochasticProcess<Condition, Consequence>) other;

        Set<Condition> causes = new HashSet<>(this.matrix.keySet());
        causes.addAll(cast.matrix.keySet());

        Set<Consequence> effects = new HashSet<>();

        for (Condition eachCause : causes) {
            effects.addAll(this.matrix.getKeys(eachCause));
            effects.addAll(cast.matrix.getKeys(eachCause));
        }

        int size = causes.size() * effects.size();
        double sum = 0d;
        float difference;
        for (Condition eachCause : causes) {
            for (Consequence eachEffect : effects) {
                difference = this.getProbability(eachCause, eachEffect) - cast.getProbability(eachCause, eachEffect);
                sum += Math.pow(difference, 2);
            }
        }

        return 1f - (float) (sum / size);
    }

    public float getDeviationQuotient(StochasticProcess<Condition, Consequence> other) {
        MatrixStochasticProcess<Condition, Consequence> cast = (MatrixStochasticProcess<Condition, Consequence>) other;

        int obsFreqSum = 0, maxFreqSum = 0;
        Consequence effect;

        for (Condition cause : cast.matrix.keySet()) {
            effect = cast.getEffect(cause);
            obsFreqSum += this.getFrequency(cause, effect);
            maxFreqSum += this.getMaxFrequency(cause);
        }

        if (maxFreqSum < 1) {
            return 1f;
        }

        return (float) obsFreqSum / maxFreqSum;
    }

    public float getLikelihood(StochasticProcess<Condition, Consequence> other) {
        MatrixStochasticProcess<Condition, Consequence> castOther = (MatrixStochasticProcess<Condition, Consequence>) other;
        float similarity = 1f;

        Condition otherCause;
        Consequence otherEffect;
        Map<Consequence, Integer> otherRow;
        Map<Consequence, Integer> thisRow;
        Integer thisFrequency, sum;

        for (Map.Entry<Condition, Map<Consequence, Integer>> otherEntry : castOther.matrix.entrySet()) {
            otherCause = otherEntry.getKey();
            thisRow = this.matrix.get(otherCause);
            if (thisRow == null) {
                return 0f;
            }
            sum = this.getMass(otherCause);
            otherRow = otherEntry.getValue();
            for (Map.Entry<Consequence, Integer> otherSubEntry : otherRow.entrySet()) {
                otherEffect = otherSubEntry.getKey();
                thisFrequency = thisRow.get(otherEffect);
                if (thisFrequency == null || thisFrequency < 1) {
                    return 0f;
                }
                similarity *= Math.pow((float) thisFrequency / sum, otherSubEntry.getValue());
            }
        }

        return similarity;
    }

    @Override
    public void add(StochasticProcess<Condition, Consequence> other) {
        MatrixStochasticProcess<Condition, Consequence> cast = (MatrixStochasticProcess<Condition, Consequence>) other;
        NestedMapMatrix<Condition, Consequence, Integer> otherMatrix = (NestedMapMatrix<Condition, Consequence, Integer>) cast.matrix;

        Condition otherCause;
        Consequence otherEffect;
        Map<Consequence, Integer> thisRow, otherRow;
        Integer thisValue;

        for (Map.Entry<Condition, Map<Consequence, Integer>> entry : otherMatrix.entrySet()) {
            otherCause = entry.getKey();
            otherRow = entry.getValue();
            thisRow = this.matrix.getRow(otherCause);

            for (Map.Entry<Consequence, Integer> subEntry : otherRow.entrySet()) {
                otherEffect = subEntry.getKey();
                thisValue = thisRow.get(otherEffect);
                if (thisValue == null) {
                    thisValue = 0;
                }
                thisRow.put(otherEffect, thisValue + subEntry.getValue());
            }
        }
    }

    @Override
    public float getProbability(Condition cause, Consequence effect) {
        int mass = this.getMass(cause);
        if (mass < 1) {
            return 1f;
        }
        return (float) this.getFrequency(cause, effect) / mass;
    }
}
