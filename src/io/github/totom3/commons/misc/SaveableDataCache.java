package io.github.totom3.commons.misc;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Totom3
 * @param <K>
 * @param <V>
 */
public abstract class SaveableDataCache<K, V> extends DataCache<K, V> {

    private static final Logger LOGGER = Logger.getLogger(SaveableDataCache.class.getName());

    public boolean trySave(V element) {
	try {
	    save(element);
	    return true;
	} catch (IOException ex) {
	    LOGGER.log(Level.SEVERE, "Could not save element " + element, ex);
	    return false;
	}
    }

    public abstract void save(V element) throws IOException;

    public void saveAll() {
	for (Entry<K, V> entry : all().entrySet()) {
	    K key = entry.getKey();
	    V value = entry.getValue();

	    try {
		save(value);
	    } catch (IOException ex) {
		new IOException("Could not save value from entry [key=" + key + ", value=" + value + "]", ex).printStackTrace();
	    }
	}
    }

}
