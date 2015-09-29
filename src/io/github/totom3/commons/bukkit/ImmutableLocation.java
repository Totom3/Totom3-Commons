package io.github.totom3.commons.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Totom3
 */
public class ImmutableLocation extends Location {

    private static String msg = "Cannot modify ImmutableLocation";

    public static ImmutableLocation of(Location copy) {
	return new ImmutableLocation(copy.getWorld(), copy.getX(), copy.getY(), copy.getZ(), copy.getYaw(), copy.getPitch());
    }
    
    public ImmutableLocation(World world, double x, double y, double z) {
	super(world, x, y, z);
    }

    public ImmutableLocation(World world, double x, double y, double z, float yaw, float pitch) {
	super(world, x, y, z, yaw, pitch);
    }

    @Override
    public Location clone() {
	return new ImmutableLocation(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
    }

    @Override
    public Location zero() {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location subtract(double x, double y, double z) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location subtract(Vector vec) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location add(double x, double y, double z) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location add(Vector vec) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location add(Location vec) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public Location setDirection(Vector vector) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setPitch(float pitch) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setYaw(float yaw) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setZ(double z) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setY(double y) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setX(double x) {
	throw new UnsupportedOperationException(msg);
    }

    @Override
    public void setWorld(World world) {
	throw new UnsupportedOperationException(msg);
    }
}
