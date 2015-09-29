package io.github.totom3.commons.misc;

import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.BukkitUtils;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Totom3
 * @param <T>
 */
public class TimerManager<T> {

    private static int currentTick() {
	return BukkitUtils.getCurrentTick();
    }

    protected final Map<T, Integer> timersStart;

    public TimerManager() {
	this.timersStart = new HashMap<>();
    }

    public TimerManager(Map<T, Integer> map) {
	this.timersStart = map;
    }

    /**
     * Returns the timer value for the specified token.
     * <p>
     * @param token the token to check. Cannot be {@code null}.
     * <p>
     * @return the number of ticks passed since the start of the timer for the
     *         specified token, or {@code 0} if the timer wasn't start, or if
     *         {@code token} is {@code null}.
     */
    public int getTimer(T token) {
	if (token == null) {
	    return 0;
	}

	Integer start = get0(token);
	if (start == null) {
	    return 0;
	}

	return currentTick() - start;
    }

    /**
     * Starts the timer for the specified token.
     * <p>
     * @param token the token to start the timer for. Must not be {@code null}.
     * <p>
     * @return {@code true} if a timer was already started (and restarted),
     *         {@code false} otherwise.
     */
    public boolean startTimer(T token) {
	checkNotNull(token);

	int tick = currentTick();
	return set0(token, tick) != null;
    }

    public int stopTimer(T token) {
	if (token == null) {
	    return 0;
	}

	Integer start = remove0(token);
	if (start == null) {
	    return 0;
	}

	return currentTick() - start;
    }

    public boolean hasTimer(T token) {
	if (token == null) {
	    return false;
	}

	return get0(token) != null;
    }
    
    protected Integer get0(T token) {
	return timersStart.get(token);
    }

    protected Integer set0(T token, int tick) {
	return timersStart.put(token, tick);
    }

    protected Integer remove0(T token) {
	return timersStart.remove(token);
    }
}
