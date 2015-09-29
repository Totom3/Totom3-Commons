package io.github.totom3.commons.collect;

import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Totom3
 * @param <E>
 * @param <F>
 */
public class ComputingSet<E, F> extends ComputingCollection<E, F> implements Set<E>{

    public ComputingSet(Set<F> deleguate, Function<E, F> interner, Function<F, E> externer) {
	super(deleguate, interner, externer);
    }

    protected ComputingSet(Set<F> deleguate) {
	super(deleguate);
    }

}
