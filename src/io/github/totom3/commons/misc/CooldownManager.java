package io.github.totom3.commons.misc;

import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.BukkitUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Totom3
 * @param <T>
 */
public class CooldownManager<T> {

    protected static int currentTick() {
	return BukkitUtils.getCurrentTick();
    }

    protected final Map<T, Integer> cooldownsEnd;

    public CooldownManager() {
	this(new HashMap<>());
    }

    public CooldownManager(Map<T, Integer> map) {
	this.cooldownsEnd = map;
    }

    public boolean hasRemaining(T token) {
	if (token == null) {
	    return false;
	}

	Integer cdEnd = get0(token);
	if (cdEnd == null) {
	    return false;
	}

	if (currentTick() >= cdEnd) {
	    remove(token);
	    return false;
	}

	return true;
    }

    public int getRemaining(T token) {
	if (token == null) {
	    return 0;
	}

	Integer cdEnd = get0(token);
	if (cdEnd == null) {
	    return 0;
	}

	int currentTick = currentTick();
	if (currentTick >= cdEnd) {
	    remove0(token);
	    return 0;
	}

	return cdEnd - currentTick;
    }

    public void setRemaining(T token, int delayInTicks) {
	checkNotNull(token);
	if (delayInTicks == 0) {
	    return;
	}
	if (delayInTicks < 0) {
	    throw new IllegalArgumentException("Cannot set negative delay " + delayInTicks);
	}

	set0(token, delayInTicks);
    }

    public int remove(T token) {
	if (token == null) {
	    return 0;
	}

	Integer cdEnd = remove0(token);
	if (cdEnd == null) {
	    return 0;
	}

	int currentTick = currentTick();
	if (currentTick >= cdEnd) {
	    return 0;
	}

	return cdEnd - currentTick;
    }

    public void cleanup() {
	int currentTick = currentTick();
	for (Iterator<Integer> it = cooldownsEnd.values().iterator(); it.hasNext();) {
	    int end = it.next();
	    if (currentTick >= end) {
		it.remove();
	    }
	}
    }

    protected boolean isValid0(T token, int cdEnd) {
	return currentTick() < cdEnd;
    }

    protected Integer get0(T token) {
	return cooldownsEnd.get(token);
    }

    protected Integer remove0(T token) {
	return cooldownsEnd.remove(token);
    }

    protected Integer set0(T token, int delay) {
	return cooldownsEnd.put(token, currentTick() + delay);
    }
}
