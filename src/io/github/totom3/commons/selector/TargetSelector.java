package io.github.totom3.commons.selector;

import io.github.totom3.commons.misc.WrappedStream;
import io.github.totom3.commons.selector.TargetSelectorArgument.TargetData;
import io.github.totom3.commons.selector.TargetSelectorSettings.LimitType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 *
 * @author Totom3
 */
public class TargetSelector {

    protected final Location origin;
    protected final TargetSelectorSettings settings;
    protected final EnumMap<TargetSelectorArgument, Object> args;

    public TargetSelector(TargetSelectorSettings settings, Map<TargetSelectorArgument, Object> args, Location origin) {
	this.settings = ImmutableTargetSelectorSettings.wrap(settings);
	this.args = (args.isEmpty()) ? new EnumMap<>(TargetSelectorArgument.class) : new EnumMap<>(args);
	this.origin = origin;
    }

    public TargetSelectorSettings getSettings() {
	return settings;
    }

    public Map<TargetSelectorArgument, Object> getArguments() {
	return Collections.unmodifiableMap(args);
    }

    public <T> Optional<T> getArgument(TargetSelectorArgument arg, Class<T> expected) {
	Object obj = args.get(arg);
	if (obj != null && !expected.isInstance(obj)) {
	    throw new ClassCastException("Expected " + expected.getName() + " for arg " + arg + ", got instead " + obj.getClass().getName());
	}

	return Optional.ofNullable((T) obj);
    }

    public List<Entity> process(Location origin) {
	if (origin == null) {
	    origin = this.origin;
	}
	origin = origin.clone();

	Collection<? extends Entity> source = getSource(origin);
	Stream<? extends Entity> stream = new WrappedStream<>(source.stream());

	filterManual(stream, origin);

	filterTestable(stream, origin);

	sortAndLimit(stream, origin);

	ArrayList<Entity> result = new ArrayList<>();

	if (settings.getLimitType() == LimitType.RANDOM) {
	    int c = getCount();
	    int s = Math.min(source.size(), c);
	    Set<Integer> accepted = new HashSet<>(c);
	    Random r = new Random();
	    while (accepted.size() < c) {
		accepted.add(r.nextInt(s));
	    }
	    stream.forEach(new Consumer<Entity>() {
		int i = 0;

		@Override
		public void accept(Entity e) {
		    if (accepted.contains(i++)) {
			result.add(e);
		    }
		}
	    });
	} else {
	    stream.forEach(result::add);
	}

	return result;
    }

    protected void sortAndLimit(Stream<? extends Entity> stream, Location origin) {
	Integer count = getCount();

	if (settings.sortsEntities()) {
	    DistanceComparator comp = new DistanceComparator(origin);
	    if (count == null || count > 0) { // If count is positive, keep closest entities
		stream.sorted(comp);
	    } else { // If count is negative, keep farthest entities
		stream.sorted(Collections.reverseOrder(comp));
	    }
	}

	if (count != null && settings.getLimitType() != LimitType.RANDOM) {
	    stream.limit(Math.abs(count));
	}
    }

    protected Collection<? extends Entity> getSource(Location origin) {
	origin = adjust(origin);

	World w = origin.getWorld();

	Optional<Number> opt = getArgument(TargetSelectorArgument.RANGE, Number.class);
	if (opt.isPresent()) {
	    double range = opt.get().doubleValue();
	    return w.getNearbyEntities(origin, range, range, range);
	}

	return (getOnlyPlayers()) ? w.getPlayers() : w.getEntities();
    }

    private boolean getOnlyPlayers() {
	Boolean b = settings.getOnlyPlayers();
	return Objects.equals(b, Boolean.TRUE) || (b == null && !args.containsKey(TargetSelectorArgument.TYPE));
    }

    protected Integer getCount() {
	return getArgument(TargetSelectorArgument.COUNT, Integer.class).orElse(settings.getDefaultCount());
    }

    private Location adjust(Location origin) {
	// SET X
	getArgument(TargetSelectorArgument.X, Number.class).ifPresent((n) -> origin.setX(n.doubleValue()));
	// SET Y
	getArgument(TargetSelectorArgument.Y, Number.class).ifPresent((n) -> origin.setY(n.doubleValue()));
	// SET Z
	getArgument(TargetSelectorArgument.Z, Number.class).ifPresent((n) -> origin.setZ(n.doubleValue()));

	return origin;
    }

    private void filterManual(Stream<? extends Entity> stream, Location origin) {
	double minRange = getArgument(TargetSelectorArgument.MIN_RANGE, Number.class).orElse(0).doubleValue();
	double minRangeSquared = minRange * minRange;

	// MINIMUM RANGE
	stream.filter((e) -> origin.distanceSquared(e.getLocation()) >= minRangeSquared);
    }

    private void filterTestable(Stream<? extends Entity> stream, Location origin) {
	// only players
	boolean checkType = !getOnlyPlayers();

	for (Entry<TargetSelectorArgument, Object> entry : getArguments().entrySet()) {
	    TargetSelectorArgument arg = entry.getKey();

	    if (arg == TargetSelectorArgument.TYPE && !checkType) {
		continue;
	    }

	    Object value = entry.getValue();

	    if (arg.isTestable()) {
		stream.filter((e) -> arg.test(new TargetData(value, e, origin)));
	    }
	}
    }

    private static class DistanceComparator implements Comparator<Entity> {

	private final Location origin;

	DistanceComparator(Location origin) {
	    this.origin = origin;
	}

	@Override
	public int compare(Entity e1, Entity e2) {
	    return (int) (origin.distanceSquared(e2.getLocation()) - origin.distanceSquared(e1.getLocation()));
	}
    }
}
