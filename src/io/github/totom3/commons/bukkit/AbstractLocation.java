package io.github.totom3.commons.bukkit;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import static java.lang.Math.abs;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Totom3
 */
public abstract class AbstractLocation implements Cloneable {

    public static AbstractLocation create(Location loc) {	
	return new BasicAbstractLocation(loc);
    }

    public static AbstractLocation create(double x, double y, double z) {
	return new BasicAbstractLocation(x, y, z, 0f, 0f);
    }

    public static AbstractLocation create(double x, double y, double z, float pitch, float yaw) {
	return new BasicAbstractLocation(x, y, z, pitch, yaw);
    }

    public static AbstractLocation unmodifiable(AbstractLocation loc) {
	return new ImmutableAbstractLocation(loc);
    }

    public static AbstractLocation createImmutable(double x, double y, double z) {
	return new ImmutableAbstractLocation(new BasicAbstractLocation(x, y, z, 0f, 0f));
    }

    public static AbstractLocation createImmutable(double x, double y, double z, float pitch, float yaw) {
	return new ImmutableAbstractLocation(new BasicAbstractLocation(x, y, z, pitch, yaw));
    }

    private static double square(double d) {
	return d * d;
    }

    public Location forWorld(World world) {
	return new Location(Preconditions.checkNotNull(world, "World cannot be null"), getX(), getY(), getZ(), getYaw(), getPitch());
    }

    public Block getBlock(World world) {
	return forWorld(world).getBlock();
    }

    public abstract double getX();

    public abstract void setX(double x);

    public abstract double getY();

    public abstract void setY(double y);

    public abstract double getZ();

    public abstract void setZ(double z);

    public abstract float getPitch();

    public abstract void setPitch(float pitch);

    public abstract float getYaw();

    public abstract void setYaw(float yaw);

    public double taxicabDistance(AbstractLocation other) {
	return abs(getX() - other.getX()) + abs(getY() - other.getY()) + abs(getZ() - other.getZ());
    }

    public double distanceSquared(AbstractLocation other) {
	return square(getX() - other.getX()) + square(getY() - other.getY()) + square(getZ() - other.getZ());
    }

    public double distance(AbstractLocation other) {
	return Math.sqrt(distanceSquared(other));
    }

    public AbstractLocation add(AbstractLocation other) {
	this.setX(this.getX() + other.getX());
	this.setY(this.getY() + other.getY());
	this.setZ(this.getZ() + other.getZ());
	this.setPitch(this.getPitch() + other.getPitch());
	this.setYaw(this.getYaw() + other.getYaw());
	return this;
    }

    public AbstractLocation substract(AbstractLocation other) {
	this.setX(this.getX() - other.getX());
	this.setY(this.getY() - other.getY());
	this.setZ(this.getZ() - other.getZ());
	this.setPitch(this.getPitch() - other.getPitch());
	this.setYaw(this.getYaw() - other.getYaw());
	return this;
    }

    public AbstractLocation multiplyBy(AbstractLocation other) {
	this.setX(this.getX() * other.getX());
	this.setY(this.getY() * other.getY());
	this.setZ(this.getZ() * other.getZ());
	this.setPitch(this.getPitch() * other.getPitch());
	this.setYaw(this.getYaw() * other.getYaw());
	return this;
    }

    public AbstractLocation divideBy(AbstractLocation other) {
	this.setX(this.getX() / other.getX());
	this.setY(this.getY() / other.getY());
	this.setZ(this.getZ() / other.getZ());
	this.setPitch(this.getPitch() / other.getPitch());
	this.setYaw(this.getYaw() / other.getYaw());
	return this;
    }

    public AbstractLocation middlePoint(AbstractLocation other) {
	return new BasicAbstractLocation(
		(getX() + other.getX()) / 2,
		(getY() + other.getY()) / 2,
		(getZ() + other.getZ()) / 2,
		(getPitch() + other.getPitch()) / 2,
		(getYaw() + other.getYaw()) / 2
	);
    }

    @Override
    public abstract AbstractLocation clone();

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}

	if (!(obj instanceof AbstractLocation)) {
	    return false;
	}

	AbstractLocation other = (AbstractLocation) obj;

	return other.getX() == getX()
		&& other.getY() == getY()
		&& other.getZ() == getZ()
		&& other.getPitch() == getPitch()
		&& other.getYaw() == getYaw();
    }

    @Override
    public String toString() {
	return "AbstractLocation{"
		+ "x=" + getX()
		+ ", y=" + getY()
		+ ", z=" + getZ()
		+ ", yaw=" + getYaw()
		+ ", pitch=" + getPitch()
		+ '}';
    }

    @Override
    public int hashCode() {
	double d;
	float f;
	int hash = 7;
	
	d = getX();
	hash = 31 * hash + (int) (Double.doubleToLongBits(d) ^ (Double.doubleToLongBits(d) >>> 32));
	
	d = getY();
	hash = 31 * hash + (int) (Double.doubleToLongBits(d) ^ (Double.doubleToLongBits(d) >>> 32));
	
	d = getZ();
	hash = 31 * hash + (int) (Double.doubleToLongBits(d) ^ (Double.doubleToLongBits(d) >>> 32));
	
	f = getPitch();
	hash = 31 * hash + Float.floatToIntBits(f);
	
	f = getYaw();
	hash = 31 * hash + Float.floatToIntBits(f);
	
	return hash;
    }

    static class BasicAbstractLocation extends AbstractLocation {

	private static double check(double d, String name) {
	    if (!Doubles.isFinite(d)) {
		throw new IllegalArgumentException(name + " must be finite; cannot be " + d);
	    }
	    return d;
	}

	private static float check(float f, String name) {
	    if (!Floats.isFinite(f)) {
		throw new IllegalArgumentException(name + " must be finite; cannot be " + f);
	    }
	    return f;
	}

	double x;
	double y;
	double z;
	float pitch;
	float yaw;

	BasicAbstractLocation(double x, double y, double z, float pitch, float yaw) {
	    this.x = check(x, "X coordinate");
	    this.y = check(y, "Y coordinate");
	    this.z = check(z, "Z coordinate");
	    this.pitch = check(pitch, "Pitch");
	    this.yaw = check(yaw, "Yaw");
	}

	BasicAbstractLocation(Location loc) {
	    this.x = loc.getX();
	    this.y = loc.getY();
	    this.z = loc.getZ();
	    this.pitch = loc.getPitch();
	    this.yaw = loc.getYaw();
	}

	@Override
	public double getX() {
	    return x;
	}

	@Override
	public void setX(double x) {
	    Preconditions.checkArgument(Doubles.isFinite(x), "X coordinate cannot be " + x + "; must be finite.");

	    this.x = x;
	}

	@Override
	public double getY() {
	    return y;
	}

	@Override
	public void setY(double y) {
	    Preconditions.checkArgument(Doubles.isFinite(y), "Y coordinate cannot be " + y + "; must be finite.");

	    this.y = y;
	}

	@Override
	public double getZ() {
	    return z;
	}

	@Override
	public void setZ(double z) {
	    Preconditions.checkArgument(Doubles.isFinite(z), "Z coordinate cannot be " + z + "; must be finite.");

	    this.z = z;
	}

	@Override
	public float getPitch() {
	    return pitch;
	}

	@Override
	public void setPitch(float pitch) {
	    Preconditions.checkArgument(Floats.isFinite(pitch), "Pitch cannot be " + pitch + "; must be finite.");

	    this.pitch = pitch;
	}

	@Override
	public float getYaw() {
	    return yaw;
	}

	@Override
	public void setYaw(float yaw) {
	    Preconditions.checkArgument(Floats.isFinite(yaw), "Yaw cannot be " + yaw + "; must be finite.");

	    this.yaw = yaw;
	}

	@Override
	public AbstractLocation clone() {
	    return new BasicAbstractLocation(getX(), getY(), getZ(), getPitch(), getYaw());
	}
    }

    static class ImmutableAbstractLocation extends AbstractLocation {

	final AbstractLocation deleguate;

	ImmutableAbstractLocation(AbstractLocation deleguate) {
	    this.deleguate = checkNotNull(deleguate);
	}

	@Override
	public String toString() {
	    return "Immutable" + super.toString();
	}

	@Override
	public Location forWorld(World world) {
	    return deleguate.forWorld(world);
	}

	@Override
	public Block getBlock(World world) {
	    return deleguate.getBlock(world);
	}

	@Override
	public double getX() {
	    return deleguate.getX();
	}

	@Override
	public double getY() {
	    return deleguate.getY();
	}

	@Override
	public double getZ() {
	    return deleguate.getZ();
	}

	@Override
	public float getPitch() {
	    return deleguate.getPitch();
	}

	@Override
	public float getYaw() {
	    return deleguate.getYaw();
	}

	@Override
	public void setX(double x) {
	    throw new UnsupportedOperationException("Cannot modify ImmutableAbstractLocation");
	}

	@Override
	public void setY(double y) {
	    throw new UnsupportedOperationException("Cannot modify ImmutableAbstractLocation");
	}

	@Override
	public void setZ(double z) {
	    throw new UnsupportedOperationException("Cannot modify ImmutableAbstractLocation");
	}

	@Override
	public void setPitch(float pitch) {
	    throw new UnsupportedOperationException("Cannot modify ImmutableAbstractLocation");
	}

	@Override
	public void setYaw(float yaw) {
	    throw new UnsupportedOperationException("Cannot modify ImmutableAbstractLocation");
	}

	@Override
	public double taxicabDistance(AbstractLocation other) {
	    return deleguate.taxicabDistance(other);
	}

	@Override
	public double distanceSquared(AbstractLocation other) {
	    return deleguate.distanceSquared(other);
	}

	@Override
	public double distance(AbstractLocation other) {
	    return deleguate.distance(other);
	}

	@Override
	public AbstractLocation add(AbstractLocation other) {
	    return deleguate.add(other);
	}

	@Override
	public AbstractLocation substract(AbstractLocation other) {
	    return deleguate.substract(other);
	}

	@Override
	public AbstractLocation multiplyBy(AbstractLocation other) {
	    return deleguate.multiplyBy(other);
	}

	@Override
	public AbstractLocation divideBy(AbstractLocation other) {
	    return deleguate.divideBy(other);
	}

	@Override
	public AbstractLocation middlePoint(AbstractLocation other) {
	    return deleguate.middlePoint(other);
	}

	@Override
	public AbstractLocation clone() {
	    return new ImmutableAbstractLocation(deleguate);
	}
    }
}
