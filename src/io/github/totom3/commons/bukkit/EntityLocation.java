package io.github.totom3.commons.bukkit;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 *
 * @author Totom3
 */
public class EntityLocation extends Location {

    private static boolean equalAndNonNull(Entity o1, Entity o2) {
	return (o1 == null || o2 == null)
		? false
		: o1.equals(o2);
    }

    private final WeakReference<? extends Entity> entity;

    public EntityLocation(Entity entity) {
	super(entity.getWorld(),
		entity.getLocation().getX(),
		entity.getLocation().getY(),
		entity.getLocation().getZ(),
		entity.getLocation().getYaw(),
		entity.getLocation().getPitch()
	);

	Preconditions.checkNotNull(entity, "Entity cannot be null");

	this.entity = new WeakReference<>(entity);
    }

    public EntityLocation(Entity entity, World world, double x, double y, double z, float yaw, float pitch) {
	super(world, x, y, z, yaw, pitch);

	this.entity = new WeakReference<>(entity);
    }

    public EntityLocation(EntityLocation loc) {
	super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

	Entity ent = loc.entity.get();

	Preconditions.checkState(ent != null, "Cannot copy location from EntityLocation without entity");

	this.entity = new WeakReference<>(ent);
    }
    
    @Override
    public EntityLocation clone() {
	return new EntityLocation(this);
    }

    private void update() {
	Entity ent = entity.get();
	if (ent != null) {
	    Location loc = ent.getLocation();

	    super.setWorld(loc.getWorld());
	    super.setX(loc.getX());
	    super.setY(loc.getY());
	    super.setZ(loc.getZ());
	    super.setYaw(loc.getYaw());
	    super.setPitch(loc.getPitch());
	}
    }

    private void updateToEntity() {
	Entity ent = entity.get();
	if (ent != null) {
	    ent.teleport(this);
	}
    }

    @Override
    public Vector toVector() {
	update();

	return super.toVector();
    }

    @Override
    public String toString() {
	update();

	return super.toString();
    }

    @Override
    public int hashCode() {
	update();

	return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}

	if (obj == null || !(obj instanceof EntityLocation)) {
	    return false;
	}

	update();

	EntityLocation other = (EntityLocation) obj;

	return equalAndNonNull(entity.get(), other.entity.get())
		|| (super.getX() == other.getX()
		&& super.getY() == other.getY()
		&& super.getZ() == other.getZ()
		&& super.getYaw() == other.getYaw()
		&& super.getPitch() == other.getPitch()
		&& super.getWorld().equals(other.getWorld()));
    }

    /**
     * Throws {@code UnsupportedOperationException}.
     *
     * @return
     */
    @Override
    public EntityLocation zero() {
	throw new UnsupportedOperationException();
    }

    @Override
    public EntityLocation multiply(double m) {
	throw new UnsupportedOperationException();
    }

    @Override
    public double distanceSquared(Location o) {
	update();

	return super.distanceSquared(o);
    }

    @Override
    public double distance(Location o) {
	update();

	return super.distance(o);
    }

    @Override
    public double lengthSquared() {
	update();

	return super.lengthSquared();
    }

    @Override
    public double length() {
	update();

	return super.length();
    }

    @Override
    public EntityLocation subtract(double x, double y, double z) {
	update();
	super.subtract(x, y, z);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation subtract(Vector vec) {
	update();
	super.subtract(vec);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation subtract(Location vec) {
	update();
	super.subtract(vec);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation add(double x, double y, double z) {
	update();
	super.add(x, y, z);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation add(Vector vec) {
	update();
	super.add(vec);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation add(Location vec) {
	update();
	super.add(vec);
	updateToEntity();
	return this;
    }

    @Override
    public EntityLocation setDirection(Vector vector) {
	update();
	super.setDirection(vector);
	updateToEntity();
	return this;
    }

    @Override
    public Vector getDirection() {
	update();
	return super.getDirection();
    }

    @Override
    public float getPitch() {
	Entity ent = entity.get();
	if (ent != null) {
	    return ent.getLocation().getPitch();
	} else {
	    return super.getPitch();
	}
    }

    @Override
    public void setPitch(float pitch) {
	update();

	super.setPitch(pitch);

	updateToEntity();
    }

    @Override
    public float getYaw() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getLocation().getPitch();
	} else {
	    return super.getPitch();
	}
    }

    @Override
    public void setYaw(float yaw) {
	update();

	super.setPitch(yaw);

	updateToEntity();
    }

    @Override
    public double getX() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getLocation().getX();
	} else {
	    return super.getX();
	}
    }

    @Override
    public double getY() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getLocation().getY();
	} else {
	    return super.getY();
	}
    }

    @Override
    public double getZ() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getLocation().getZ();
	} else {
	    return super.getZ();
	}
    }

    @Override
    public void setX(double x) {
	super.setX(x);
    }

    @Override
    public void setY(double y) {
	super.setY(y);
    }

    @Override
    public void setZ(double z) {
	super.setZ(z);
    }

    @Override
    public int getBlockX() {
	update();

	return super.getBlockX();
    }

    @Override
    public int getBlockY() {
	update();

	return super.getBlockY();
    }

    @Override
    public int getBlockZ() {
	update();

	return super.getBlockZ();
    }

    @Override
    public Block getBlock() {
	update();

	return super.getBlock();
    }

    @Override
    public Chunk getChunk() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getLocation().getChunk();
	} else {
	    return super.getChunk();
	}
    }

    @Override
    public World getWorld() {
	Entity ent = entity.get();

	if (ent != null) {
	    return ent.getWorld();
	} else {
	    return super.getWorld();
	}
    }

    @Override
    public void setWorld(World world) {
	update();

	super.setWorld(world);

	updateToEntity();
    }
}
