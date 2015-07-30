package net.sophomatics.markov_predictor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mark on 14.07.15.
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

    @Override
    public Iterator<MarkovPredictor<Condition, Consequence>> iterator() {
        return this.products.iterator();
    }
}
