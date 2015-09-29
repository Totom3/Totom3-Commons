package io.github.totom3.commons.collect;

import java.util.Comparator;
import java.util.SortedMap;

/**
 *
 * @author Totom3
 */
public abstract class FilteredSortedMap<K, V> extends FilteredMap<K, V> implements SortedMap<K, V> {

    @Override
    protected abstract SortedMap<K, V> map();

    @Override
    public Comparator<? super K> comparator() {
	return map().comparator();
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
	return map().subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
	return map().headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
	return map().tailMap(fromKey);
    }

    @Override
    public K firstKey() {
	return map().firstKey();
    }

    @Override
    public K lastKey() {
	return map().lastKey();
    }
}
