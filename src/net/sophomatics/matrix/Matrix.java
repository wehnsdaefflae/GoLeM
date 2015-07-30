package net.sophomatics.matrix;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mark on 12.07.15.
 */
public interface Matrix<A, B, C> extends Map<A, Map<B, C>> {
    C put(A k0, B k1, C v);
    C get(A k0, B k1);
    Map<B, C> getRow(A key);
    void integrate(Matrix<A, B, C> other);
    String print();
    Set<B> getKeys(A k);
    List<C> getValues(A k);
}
