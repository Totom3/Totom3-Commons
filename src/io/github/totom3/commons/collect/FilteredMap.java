package io.github.totom3.commons.collect;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Totom3
 * @param <K>
 * @param <V>
 */
public abstract class FilteredMap<K, V> extends AbstractMap<K, V> {

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public FilteredMap() {
	keyClass = null;
	valueClass = null;
    }

    public FilteredMap(Class<K> keyClass, Class<V> valueClass) {
	this.keyClass = keyClass;
	this.valueClass = valueClass;
    }

    @Override
    public final int size() {
	return map().size();
    }

    @Override
    public final boolean containsKey(Object key) {
	if (!mightContainKey(key)) {
	    return false;
	}

	return map().containsKey(key);
    }

    @Override
    public final boolean containsValue(Object value) {
	if (!mightContainValue(value)) {
	    return false;
	}

	return map().containsValue(value);
    }

    @Override
    public final V get(Object key) {
	return map().get(key);
    }

    @Override
    public final V put(K key, V value) {
	checkPairOrThrow(key, value);

	return map().put(key, value);
    }

    @Override
    public final V remove(Object key) {
	return map().remove(key);
    }

    @Override
    public final void clear() {
	map().clear();
    }

    @Override
    public final Set<K> keySet() {
	return new FilteredSet<K>(keyClass) {

	    @Override
	    protected Set<K> collection() {
		return map().keySet();
	    }

	    @Override
	    protected boolean check(K key) {
		return checkKey(key);
	    }

	    @Override
	    protected void checkOrThrow(K key) {
		checkKeyOrThrow(key);
	    }
	};
    }

    @Override
    public final Collection<V> values() {
	return new FilteredCollection<V>(valueClass) {

	    @Override
	    protected Collection<V> collection() {
		return map().values();
	    }

	    @Override
	    protected boolean check(V value) {
		return checkValue(value);
	    }

	    @Override
	    protected void checkOrThrow(V value) {
		checkValueOrThrow(value);
	    }
	};
    }

    @Override
    public final Set<Entry<K, V>> entrySet() {
	return new FilteredSet<Entry<K, V>>() {

	    @Override
	    protected Set<Entry<K, V>> collection() {
		return map().entrySet();
	    }

	    @Override
	    protected boolean check(Entry<K, V> entry) {
		return checkPair(entry.getKey(), entry.getValue());
	    }

	    @Override
	    protected void checkOrThrow(Entry<K, V> entry) {
		checkPairOrThrow(entry.getKey(), entry.getValue());
	    }

	};
    }

    protected boolean mightContainKey(Object key) {
	if (key != null && keyClass != null && keyClass.isInstance(key)) {
	    return checkKey((K) key);
	}

	return true;
    }

    protected boolean mightContainValue(Object value) {
	if (value != null && valueClass != null && valueClass.isInstance(value)) {
	    return checkValue((V) value);
	}

	return true;
    }

    protected abstract Map<K, V> map();

    protected abstract boolean checkKey(K key);

    protected abstract boolean checkValue(V value);

    protected boolean checkPair(K key, V value) {
	return checkKey(key) && checkValue(value);
    }

    protected void checkKeyOrThrow(K key) {
	if (!checkKey(key)) {
	    throw new IllegalArgumentException();
	}
    }

    protected void checkValueOrThrow(V value) {
	if (!checkValue(value)) {
	    throw new IllegalArgumentException();
	}
    }

    protected void checkPairOrThrow(K key, V value) {
	if (!checkPair(key, value) || !checkKey(key) || !checkValue(value)) {
	    throw new IllegalArgumentException();
	}
    }
}
