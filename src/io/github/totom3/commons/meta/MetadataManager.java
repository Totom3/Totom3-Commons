package io.github.totom3.commons.meta;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Totom3
 * @param <T>
 */
public class MetadataManager<T> {

    protected final Map<T, Map<Object, Object>> metadata;

    public MetadataManager() {
	metadata = new HashMap<>();
    }

    protected MetadataManager(Map<T, Map<Object, Object>> map) {
	this.metadata = map;
    }

    public Object get(T token, Object key) {
	Map<Object, Object> map = metadata.get(token);
	if (map == null) {
	    return null;
	}

	return map.get(key);
    }

    public <E> E getUnchecked(T token, Object key) {
	return (E) get(token, key);
    }

    public Map<Object, Object> removeAll(T token) {
	return metadata.remove(token);
    }

    public Object remove(T token, Object key) {
	Map<Object, Object> map = metadata.get(token);
	if (map == null) {
	    return null;
	}

	Object removed = map.remove(key);
	if (map.isEmpty()) {
	    metadata.remove(token);
	}

	return removed;
    }

    public Object set(T token, Object key, Object value) {
	checkNotNull(token);
	Map<Object, Object> map = metadata.get(token);
	if (map == null) {
	    if (value == null) { // stop it here because it will create a map just to remove it 
		return null;
	    }
	    map = makeMap();
	    metadata.put(token, map);
	}

	if (value == null) {
	    Object removed = map.remove(key);
	    if (map.isEmpty()) {
		metadata.remove(token);
	    }

	    return removed;
	}

	return map.put(key, value);
    }

    public boolean hasMetadata(T token) {
	Map<Object, Object> map = metadata.get(token);
	if (map == null) {
	    return false;
	}

	if (map.isEmpty()) {
	    metadata.remove(token);
	    return false;
	}

	return true;
    }

    public boolean hasMetadata(T token, Object key) {
	Map<Object, Object> map = metadata.get(token);
	if (map == null) {
	    return false;
	}

	if (map.isEmpty()) {
	    metadata.remove(token);
	    return false;
	}

	return map.get(key) != null;
    }

    protected Map<Object, Object> makeMap() {
	return new HashMap<>();
    }
}
