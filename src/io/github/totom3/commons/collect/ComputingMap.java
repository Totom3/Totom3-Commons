package io.github.totom3.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Totom3
 * @param <K>
 * @param <V>
 * @param <W>
 */
public class ComputingMap<K, V, W> extends AbstractMap<K, V> {

    private final Map<K, W> deleguate;

    private final Function<V, W> interner;

    private final Function<W, V> externer;

    protected ComputingMap(Map<K, W> deleguate) {
	this.deleguate = checkNotNull(deleguate);
	this.interner = null;
	this.externer = null;
    }

    public ComputingMap(Map<K, W> deleguate, Function<V, W> interner, Function<W, V> externer) {
	this.deleguate = checkNotNull(deleguate);
	this.interner = interner;
	this.externer = externer;
    }

    private W intern(Object value) {
	if (value == null) {
	    return null;
	}
	if (interner == null) {
	    return null;
	}
	return interner.apply((V) value);
    }

    private V extern(Object value) {
	if (value == null) {
	    return null;
	}
	if (externer == null) {
	    return null;
	}
	return externer.apply((W) value);
    }

    @Override
    public int size() {
	return deleguate.size();
    }

    @Override
    public boolean isEmpty() {
	return deleguate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
	return deleguate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return deleguate.containsValue(intern(value));
    }

    @Override
    public V get(Object key) {
	return extern(deleguate.get(intern(key)));
    }

    @Override
    public V put(K key, V value) {
	return extern(deleguate.put(key, intern(value)));
    }

    @Override
    public V remove(Object key) {
	return extern(deleguate.remove(intern(key)));
    }

    @Override
    public void clear() {
	deleguate.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
	return new ComputingSet<>(
		deleguate.entrySet(),
		(e) -> new AbstractMap.SimpleEntry<>(e.getKey(), intern(e.getValue())),
		(e) -> new AbstractMap.SimpleEntry<>(e.getKey(), extern(e.getValue())));
    }

}
