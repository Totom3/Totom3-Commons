package io.github.totom3.commons.misc;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 *
 * @author Totom3
 * @param <E>
 */
public class DataBank<E> {

    protected final BiMap<Short, E> cache;

    private final IdSupplier idSupplier = new IdSupplier();

    public DataBank() {
	cache = HashBiMap.create();
    }

    /**
     * Returns the ID associated with a value present in this {@code DataBank}.
     * If the provided one is not present in the cache, {@code null} is
     * returned.
     *
     * @param value the value to get the ID of.
     *
     * @return the ID of the provided value, or {@code null} if said value is
     *         not present.
     *
     * @throws NullPointerException if {@code value} is {@code null} and this
     *                              implementation does not accept {@code null}
     *                              values.
     */
    public short getByValue(E value) {
	checkNotNull(value);
	return cache.inverse().get(value);
    }

    /**
     * Returns the value associated with the specified ID, or {@code null} if
     * none is.
     *
     * @param id the ID of the value to get.
     *
     * @return the value with the specified ID, or {@code null} is none is.
     */
    public E get(short id) {
	E val = cache.get(id);
	return (val == null)
		? null
		: extern(val, id);
    }

    /**
     * Checks whether the specified value is contained in this {@code DataBank}.
     *
     * @param val the value to check.
     *
     * @return {@code true} if a value is set for the specified value,
     *         {@code false} otherwise.
     */
    public boolean containsValue(E val) {
	if (!isValueValid(val)) {
	    return false;
	}
	return cache.inverse().get(val) != null;
    }

    public boolean containsID(short key) {
	return cache.get(key) != null;
    }

    public void insert(short key, E val) {
	if (cache.containsKey(key)) {
	    throw new IllegalArgumentException("Key already present: " + key);
	}
	if (cache.inverse().get(val) != null) {
	    throw new IllegalArgumentException("Value already present: " + val);
	}
	if (!isValueValid(val)) {
	    throw new IllegalArgumentException("Invalid key: " + val);
	}
	cache.put(key, intern(val, key));
    }

    public short getOrInsert(E val) {
	return doGetOrInsert(val);
    }

    protected final short doGetOrInsert(E val) {
	if (!isValueValid(val)) {
	    throw new IllegalArgumentException("Invalid value: " + val);
	}

	Short key = cache.inverse().get(val);
	if (key != null) {
	    return key;
	}
	short newKey = nextId();
	cache.put(newKey, intern(val, newKey));
	return newKey;
    }

    public boolean removeValue(E value) {
	if (!isValueValid(value)) {
	    return false;
	}
	return doRemoveValue(value);
    }

    protected final boolean doRemoveValue(E value) {
	return cache.inverse().remove(value) != null;
    }

    public E removeKey(short key) {
	E removed = cache.remove(key);
	return (removed == null)
		? null
		: extern(removed, key);
    }

    public final Map<Short, E> all() {
	return Collections.unmodifiableMap(cache);
    }

    public final Map<E, Short> inversedAll() {
	return Collections.unmodifiableMap(cache.inverse());
    }

    public final void clear() {
	cache.clear();
    }

    public final int size() {
	return cache.size();
    }

    public int maxSize() {
	return idSupplier.get();
    }

    protected short nextId() {
	return idSupplier.get();
    }

    protected E intern(E val, short id) {
	return val;
    }

    protected E extern(E val, short id) {
	return val;
    }

    protected boolean isValueValid(E val) {
	return true;
    }

    @Override
    public String toString() {
	return cache.toString();
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 23 * hash + Objects.hashCode(this.cache);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof DataBank)) {
	    return false;
	}
	final DataBank<?> other = (DataBank<?>) obj;
	
	return cache.equals(other.cache);
    }

    static class IdSupplier {

	short current = 1;

	short get() {
	    short newId = current++;
	    if (newId == 0) {
		throw new NoSuchElementException();
	    }
	    return newId;
	}
    }

}
