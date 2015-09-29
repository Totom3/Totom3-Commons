package io.github.totom3.commons.collect;

import java.util.Comparator;
import java.util.SortedSet;

/**
 *
 * @author Totom3
 * @param <E>
 */
public abstract class FilteredSortedSet<E> extends FilteredSet<E> implements SortedSet<E> {

    @Override
    protected abstract SortedSet<E> collection();

    @Override
    public Comparator<? super E> comparator() {
	return collection().comparator();
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
	return collection().subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
	return collection().headSet(toElement);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
	return collection().tailSet(fromElement);
    }

    @Override
    public E first() {
	return collection().first();
    }

    @Override
    public E last() {
	return collection().last();
    }
}
