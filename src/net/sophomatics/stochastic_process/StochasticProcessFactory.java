package net.sophomatics.stochastic_process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a factory for Markov predictors
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 */
public class StochasticProcessFactory<Condition, Consequence> implements Iterable<StochasticProcess<Condition, Consequence>> {
    private final List<StochasticProcess<Condition, Consequence>> products;

    public StochasticProcessFactory() {
        this.products = new ArrayList<>();
    }

    public StochasticProcess<Condition, Consequence> newInstance() {
        int newId = this.products.size();
        StochasticProcess<Condition, Consequence> newProduct = new MatrixStochasticProcess<>(newId);
        this.products.add(newProduct);
        return newProduct;
    }

    public StochasticProcess<Condition, Consequence> get(int id) {
        return this.products.get(id);
    }

    public List<StochasticProcess<Condition, Consequence>> getProducts() {
        return new ArrayList<>(this.products);
    }

    @Override
    public Iterator<StochasticProcess<Condition, Consequence>> iterator() {
        return this.products.iterator();
    }
}
