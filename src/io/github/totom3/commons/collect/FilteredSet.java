package io.github.totom3.commons.collect;

import java.util.Set;

/**
 *
 * @author Totom3
 */
public abstract class FilteredSet<T> extends FilteredCollection<T> implements Set<T> {

    public FilteredSet() {
    }

    public FilteredSet(Class<T> clazz) {
	super(clazz);
    }

    @Override
    protected abstract Set<T> collection();
}
