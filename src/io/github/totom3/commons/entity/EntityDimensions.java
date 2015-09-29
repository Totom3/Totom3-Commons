package io.github.totom3.commons.entity;

import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;

/**
 *
 * @author Totom3
 */
public class EntityDimensions {

    public static EntityDimensions getDimensions(org.bukkit.entity.Entity entity) {
	if (!(entity instanceof CraftEntity)) {
	    throw new IllegalArgumentException("Entity is not a CraftEntity");
	}

	return new EntityDimensions((CraftEntity) entity);
    }

    public static double getHeight(org.bukkit.entity.Entity entity) {
	return getDimensions(entity).getHeight();
    }

    public static double getWidth(org.bukkit.entity.Entity entity) {
	return getDimensions(entity).getWidth();
    }

    private final Entity entity;

    private EntityDimensions(CraftEntity entity) {
	this.entity = entity.getHandle();

    }

    public double getHeight() {
	return entity.length;
    }

    public double getWidth() {
	return entity.width;
    }

    public org.bukkit.entity.Entity getEntity() {
	return entity.getBukkitEntity();
    }
}
