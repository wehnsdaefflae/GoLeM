package net.sophomatics.markov_predictor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a factory for Markov predictors
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 *
 */
public class MarkovPredictorFactory<Condition, Consequence> implements Iterable<MarkovPredictor<Condition, Consequence>> {
    private final List<MarkovPredictor<Condition, Consequence>> products;

    public MarkovPredictorFactory() {
        this.products = new ArrayList<>();
    }

    public MarkovPredictor<Condition, Consequence> newInstance() {
        int newId = this.products.size();
        MarkovPredictor<Condition, Consequence> newProduct = new MatrixMarkovPredictor<>(newId);
        this.products.add(newProduct);
        return newProduct;
    }

    public MarkovPredictor<Condition, Consequence> get(int id) {
        return this.products.get(id);
    }

    public List<MarkovPredictor<Condition, Consequence>> getProducts() {
        return new ArrayList<>(this.products);
    }

    @Override
    public Iterator<MarkovPredictor<Condition, Consequence>> iterator() {
        return this.products.iterator();
    }
}
