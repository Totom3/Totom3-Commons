package io.github.totom3.commons.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bukkit.World;

/**
 *
 * @author Totom3
 */
public class AbstractRegion implements Iterable<AbstractLocation> {

    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    private final int minY;
    private final int maxY;

    public AbstractRegion(AbstractLocation loc1, AbstractLocation loc2) {
	this.minX = (int) min(loc1.getX(), loc2.getX());
	this.minY = (int) min(loc1.getY(), loc2.getY());
	this.minZ = (int) min(loc1.getZ(), loc2.getZ());
	this.maxX = (int) max(loc1.getX(), loc2.getX());
	this.maxY = (int) max(loc1.getY(), loc2.getY());
	this.maxZ = (int) max(loc1.getZ(), loc2.getZ());
    }

    public AbstractRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
	this.minX = minX;
	this.maxX = maxX;
	this.minZ = minZ;
	this.maxZ = maxZ;
	this.minY = minY;
	this.maxY = maxY;
    }

    public AbstractLocation getFirst() {
	return AbstractLocation.create(minX, minY, minZ);
    }

    public AbstractLocation getSecond() {
	return AbstractLocation.create(maxX, maxY, maxZ);
    }

    public boolean englobes(AbstractLocation loc) {
	double a = loc.getX();
	if (minX > a || a > maxX) {
	    return false;
	}
	a = loc.getY();
	if (minY > a || a > maxY) {
	    return false;
	}
	a = loc.getZ();
	return !(minZ > a || a > maxZ);
    }

    public int size() {
	return (maxX - minX) + (maxY - minY) + (maxZ - minZ) + 3;
    }

    public Region forWorld(World world) {
	checkNotNull(world, "World cannot be null");

	return new Region(world, minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Iterator<AbstractLocation> iterator() {
	return new AbstractRegionIterator(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 29 * hash + this.minX;
	hash = 29 * hash + this.maxX;
	hash = 29 * hash + this.minZ;
	hash = 29 * hash + this.maxZ;
	hash = 29 * hash + this.minY;
	hash = 29 * hash + this.maxY;
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof AbstractRegion)) {
	    return false;
	}
	AbstractRegion other = (AbstractRegion) obj;

	return minX == other.minX && minY == other.minY && minZ == other.minZ
		&& maxX == other.maxX && maxY == other.maxY && maxZ == other.maxZ;
    }

    static class AbstractRegionIterator implements Iterator<AbstractLocation> {

	private int currentZ;
	private int currentX;
	private int currentY;

	private final int minX, minY;
	private final int maxX, maxY, maxZ;

	private boolean dirty;
	private boolean done;

	AbstractRegionIterator(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
	    this.minX = currentX = minX;
	    this.minY = currentY = minY;
	    this.currentZ = minZ;

	    this.maxX = maxX;
	    this.maxY = maxY;
	    this.maxZ = maxZ;
	}

	AbstractRegionIterator(AbstractRegion reg) {
	    this(reg.minX, reg.minY, reg.minZ, reg.maxX, reg.maxY, reg.maxZ);
	}

	@Override
	public boolean hasNext() {
	    if (dirty) {
		done = !(currentX <= maxX && currentY <= maxY && currentZ <= maxZ);
		dirty = false;
	    }
	    return !done;
	}

	@Override
	public AbstractLocation next() {
	    if (!hasNext()) {
		throw new NoSuchElementException("Finished iterating over Region");
	    }
	    AbstractLocation loc = AbstractLocation.create(currentX, currentY, currentZ);
	    checkAndIncrementX();
	    dirty = true;
	    return loc;
	}

	private void checkAndIncrementX() {
	    if (++currentX > maxX) {
		currentX = minX;
		checkAndIncrementY();
	    }
	}

	private void checkAndIncrementY() {
	    if (++currentY > maxY) {
		currentY = minY;
		checkAndIncrementZ();
	    }
	}

	private void checkAndIncrementZ() {
	    if (++currentZ > maxZ) {
		done = true;
	    }
	}
    }
}
