package io.github.totom3.commons.collect;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Totom3
 * @param <T>
 */
public abstract class FilteredList<T> extends FilteredCollection<T> implements List<T> {

    public FilteredList() {
    }

    public FilteredList(Class<T> clazz) {
	super(clazz);
    }

    @Override
    protected abstract List<T> collection();

    @Override
    public final T get(int index) {
	return collection().get(index);
    }

    @Override
    public final T set(int index, T element) {
	checkOrThrow(element);
	return collection().set(index, element);
    }

    @Override
    public final void add(int index, T element) {
	checkOrThrow(element);
	collection().add(index, element);
    }

    @Override
    public final boolean addAll(int index, Collection<? extends T> c) {
	List<T> list = collection();
	if (index < 0 || index >= list.size()) {
	    throw new IndexOutOfBoundsException("Index " + index + " is out of bounds: 0 to " + (list.size() - 1));
	}
	for (T obj : c) {
	    add(index++, obj);
	}
	return !c.isEmpty();
    }

    @Override
    public final T remove(int index) {
	return collection().remove(index);
    }

    @Override
    public final int indexOf(Object o) {
	if (!mightContain(o)) {
	    return -1;
	}
	return collection().indexOf(o);
    }

    @Override
    public final int lastIndexOf(Object o) {
	if (!mightContain(o)) {
	    return -1;
	}
	return collection().lastIndexOf(o);
    }

    @Override
    public final ListIterator<T> listIterator() {
	return collection().listIterator();
    }

    @Override
    public final ListIterator<T> listIterator(int index) {
	return collection().listIterator(index);
    }

    @Override
    public final List<T> subList(int fromIndex, int toIndex) {
	return collection().subList(fromIndex, toIndex);
    }
}
