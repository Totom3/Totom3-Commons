package io.github.totom3.commons.bukkit;

import com.google.common.base.Preconditions;
import io.github.totom3.commons.bukkit.AbstractRegion.AbstractRegionIterator;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Totom3
 */
public class Region implements Iterable<Location> {

    private final AbstractRegion reg;
    private final World world;

    public Region(Location loc1, Location loc2) {
	Preconditions.checkArgument(loc1.getWorld().equals(loc2.getWorld()), "The two locations must be in the same world");

	this.world = loc1.getWorld();
	this.reg = new AbstractRegion(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
    }

    public Region(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
	this.world = world;
	this.reg = new AbstractRegion(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public World getWorld() {
	return world;
    }

    public Location getFirst() {
	return reg.getFirst().forWorld(world);
    }

    public Location getSecond() {
	return reg.getSecond().forWorld(world);
    }

    public AbstractRegion asAbstract() {
	return reg;
    }

    @Override
    public Iterator<Location> iterator() {
	return new RegionIterator(world, reg);
    }

    public int size() {
	return reg.size();
    }

    static class RegionIterator implements Iterator<Location> {

	final AbstractRegionIterator iterator;
	final World world;

	RegionIterator(World world, AbstractRegion reg) {
	    this.world = world;
	    this.iterator = new AbstractRegionIterator(reg);
	}

	@Override
	public boolean hasNext() {
	    return iterator.hasNext();
	}

	@Override
	public Location next() {
	    return iterator.next().forWorld(world);
	}
    }
}
