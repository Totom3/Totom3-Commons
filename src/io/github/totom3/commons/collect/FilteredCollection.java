package io.github.totom3.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author Totom3
 * @param <T>
 */
public abstract class FilteredCollection<T> extends AbstractCollection<T> {

    private final Class<T> clazz;

    public FilteredCollection() {
	this.clazz = null;
    }

    public FilteredCollection(Class<T> clazz) {
	this.clazz = checkNotNull(clazz);
    }

    protected boolean mightContain(Object o) {
	if (o != null && clazz != null) {
	    if (!clazz.isInstance(o) || !check((T) o)) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public final int size() {
	return collection().size();
    }

    @Override
    public final boolean contains(Object o) {
	if (o != null && clazz != null) {
	    if (!clazz.isInstance(o) || !check((T) o)) {
		return false;
	    }
	}
	return collection().contains(o);
    }

    @Override
    public final Iterator<T> iterator() {
	return collection().iterator();
    }

    @Override
    public final boolean add(T obj) {
	checkOrThrow(obj);
	return collection().add(obj);
    }

    @Override
    public final boolean remove(Object o) {
	return collection().remove(o);
    }

    @Override
    public final void clear() {
	collection().clear();
    }

    @Override
    public boolean equals(Object o) {
	if (!(o instanceof Collection)) {
	    return false;
	}
	if (o == this) {
	    return true;
	}

	Collection c = (o instanceof FilteredCollection) ? ((FilteredCollection) o).collection() : (Collection) o;

	return collection().equals(c);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 43 * hash + Objects.hashCode(this.clazz);
	hash = 43 * hash + Objects.hashCode(collection());
	return hash;
    }

    protected abstract Collection<T> collection();

    protected abstract boolean check(T object);

    protected void checkOrThrow(T object) {
	if (!check(object)) {
	    throw new IllegalArgumentException();
	}
    }
}
