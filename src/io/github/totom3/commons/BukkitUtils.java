package io.github.totom3.commons;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import static java.lang.Math.toRadians;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Totom3
 */
public class BukkitUtils {

    private static final Field itemHandleField;

    static {
	try {
	    itemHandleField = CraftItemStack.class.getDeclaredField("handle");
	    itemHandleField.setAccessible(true);
	} catch (NoSuchFieldException | SecurityException ex) {
	    throw new AssertionError("Could not init item handle field", ex);
	}
    }

    public static int getCurrentTick() {
	return MinecraftServer.currentTick;
    }

    /**
     * Calculates the horizontal distance between to input {@code Location}s.
     * <p>
     * @param loc1 the first location.
     * @param loc2 the second location.
     * <p>
     * @return the distance between the two locations, without taking in
     *         consideration the Y axis.
     */
    public static double horizontalDistance(Location loc1, Location loc2) {
	return Math.sqrt(
		Math.abs(loc1.getX() - loc2.getX())
		+ Math.abs(loc1.getZ() - loc2.getZ()));
    }

    /**
     * Calculates the vertical distance between two input {@code Location}s.
     * <p>
     * @param loc1 the first location.
     * @param loc2 the second location.
     * <p>
     * @return the absolute value of the difference between {@code loc1.getY()}
     *         and {@code loc2.getY()}
     */
    public static double verticalDistance(Location loc1, Location loc2) {
	return Math.abs(loc1.getY() - loc2.getY());
    }

    public static String stripColors(String str) {
	return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str.trim()));
    }

    /**
     * Returns whether or not an entity is looking at another entity.
     * <p>
     *
     * @param looking the {@code LivingEntity} that is possibly looking at the
     *                other entity.
     * @param looked  the {@code Entity} being possibly looked at.
     * <p>
     * @return {@code true} if {@code ent1} is looking at {@code ent2}, false
     *         otherwise
     * <p>
     * @throws IllegalArgumentException if the two entities are in different
     *                                  worlds.
     */
    public static boolean isEntityLookingAt(LivingEntity looking, Entity looked) {
	final double epsilon = 2.0F;

	Location loc1 = looking.getLocation();
	Location loc2 = looked.getLocation();

	Preconditions.checkArgument(looking.getWorld().equals(looked.getWorld()), "Ent1 and Ent2 are in different worlds.");

	Location loc3 = looking.getTargetBlock((Set<Material>) null, 100).getLocation();

	double cp = ((loc2.getZ() - loc1.getZ()) * (loc3.getX() - loc1.getX())) - ((loc2.getX() - loc1.getX()) * (loc3.getZ() - loc1.getZ()));
	if (cp > epsilon) {
	    return false;
	}

	double dp = ((loc2.getX() - loc1.getX()) * (loc3.getX() - loc1.getX())) + ((loc2.getZ() - loc1.getZ()) * (loc3.getZ() - loc1.getZ()));
	if (dp < 0) {
	    return false;
	}

	double sqba = ((loc3.getX() - loc1.getX()) * (loc3.getX() - loc1.getX())) + ((loc3.getZ() - loc1.getZ()) * (loc3.getZ() - loc1.getZ()));

	return dp <= sqba;
    }

    /**
     * Returns the {@code Entity} with the given unique ID, in the specified
     * {@code World}, or {@code null} if none was found.
     * <p>
     * This is done in O(1) and does not require a linear search through the
     * entities of the world.
     * </p>
     * <p>
     * @param uniqueID the {@code UUID} of the {@code Entity}, as returned by
     *                 {@link Entity#getUniqueId()}
     * @param world    the {@code World} in which the {@code Entity} resides.
     * <p>
     * @return the {@code Entity} with the matching unique identifier, or
     *         {@code null} if none was found.
     */
    public static Entity getEntity(UUID uniqueID, World world) {
	net.minecraft.server.v1_8_R3.WorldServer w = ((CraftWorld) world).getHandle();

	return w.getEntity(uniqueID).getBukkitEntity();
    }

    public static Player getPlayer(UUID uid) {
	EntityPlayer player = MinecraftServer.getServer().getPlayerList().a(uid);
	if (player == null) {
	    return null;
	}
	return player.getBukkitEntity();
    }

    /**
     * Returns the {@code Entity} with the given unique ID, in the specified
     * {@code World}, or {@code null} if none was found.
     * <p>
     * This is done in O(1) and does not require a linear search through the
     * entities of the world.
     * </p>
     * <p>
     * @param entityID the entity's ID, as returned by
     *                 {@link Entity#getEntityId()()}
     * @param world    the {@code World} in which the {@code Entity} resides.
     * <p>
     * @return the {@code Entity} with the matching unique identifier, or
     *         {@code null} if none was found.
     */
    public static Entity getEntity(int entityID, World world) {
	net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
	net.minecraft.server.v1_8_R3.Entity entity = w.a(entityID);
	if (entity == null) {
	    return null;
	}
	return entity.getBukkitEntity();
    }

    /**
     * Returns a {@code Collection} view of the {@code LivingEntities} within
     * the specified range. If there are no nearby {@code LivingEntity}, an
     * empty {@code Collection} is returned.
     * <p>
     * @param entity
     * @param range  the spherical range of the filter
     * <p>
     * @return a {@code Collection} of all the {@code LivingEntities} within the
     *         specified range of the entity.
     */
    public static Collection<LivingEntity> getNearbyLivingEntities(Entity entity, double range) {
	List<LivingEntity> entities = new ArrayList<>(10);

	for (Entity ent : entity.getNearbyEntities(range, range, range)) {
	    if (ent instanceof LivingEntity) {
		entities.add((LivingEntity) ent);
	    }
	}
	return entities;
    }

    public static Location parseLocation(String rawLoc) throws IllegalArgumentException {
	List<String> list = Splitter.on("|").splitToList(rawLoc);
	if (list.size() < 4) {
	    throw new IllegalArgumentException("Malformed location. Expected: <world> <x> <y> <z>. Got instead: " + rawLoc);
	}
	World world = Bukkit.getWorld(list.get(0));
	double x = Double.parseDouble(list.get(1));
	double y = Double.parseDouble(list.get(2));
	double z = Double.parseDouble(list.get(3));

	return new Location(world, x, y, z);
    }

    public static String serializeBlockLocation(Location loc) throws NullPointerException {
	return loc.getWorld().getName() + '|' + loc.getBlockX() + '|' + loc.getBlockY() + '|' + loc.getBlockZ();
    }

    public static ItemStack getNMSStack(org.bukkit.inventory.ItemStack stack) {
	try {
	    ItemStack nmsStack = (ItemStack) itemHandleField.get(stack);
	    return (nmsStack != null) ? nmsStack : CraftItemStack.asNMSCopy(stack);
	} catch (IllegalArgumentException | IllegalAccessException ex) {
	    throw new AssertionError("Could not get item stack handle", ex);
	}
    }

    public static int network2DataSlot(int slot) {
	switch (slot) {
	    case 1:
	    case 2:
	    case 3:
	    case 4:
		return slot + 79;
	    case 5:
		return 103;
	    case 6:
		return 102;
	    case 7:
		return 101;
	    case 8:
		return 100;
	    case 36:
	    case 37:
	    case 38:
	    case 39:
	    case 40:
	    case 41:
	    case 42:
	    case 43:
	    case 44:
		return slot - 36;
	}
	return slot;
    }

    public static int data2NetworkSlot(int slot) {
	switch (slot) {
	    case 0:
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
	    case 8:
		return slot + 36;
	    case 80:
	    case 81:
	    case 82:
	    case 83:
		return slot - 79;
	    case 100:
		return 8;
	    case 101:
		return 7;
	    case 102:
		return 6;
	    case 103:
		return 5;
	}
	return slot;
    }

    public static Location getLookedLocation(Player p, double distance) {
	Location loc = p.getEyeLocation();
	float yaw = loc.getYaw();
	double pitch = toRadians(loc.getPitch());
	double fixedYaw = toRadians(yaw + 90);
	double cosPitch = Math.cos(pitch);
	loc.add(Math.cos(fixedYaw) * distance * cosPitch, Math.sin(-pitch) * distance, Math.sin(fixedYaw) * distance * cosPitch);
	return loc;
    }

    public static int getPing(Player player) {
	return ((CraftPlayer) player).getHandle().ping;
    }

    public static String getName(Entity entity) {
	return (entity instanceof Player) ? entity.getName() : (entity.getCustomName() != null) ? entity.getCustomName() : entity.getType().name().toLowerCase().replace('_', ' ');
    }

    public static boolean isInCreative(HumanEntity human) {
	return isCreative(human.getGameMode());
    }

    public static boolean isCreative(GameMode gameMode) {
	return gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR;
    }

    public static boolean isInFOV(Location camera, Location target, float totalFOV) {
	Location camera2 = camera.clone();
	float yaw = camera.getYaw() - (totalFOV / 2);
	Vector leftLimit = makeVector(yaw);
	Vector rightLimit = makeVector(yaw + totalFOV);
	Vector targetVector = target.toVector().subtract(camera.toVector());

	return isInMiddle(leftLimit, targetVector, rightLimit);
    }

    public static boolean isInMiddle(Vector left, Vector center, Vector right) {
	Vector nright = new Vector(right.getZ(), right.getY(), -right.getX());
	double dot1 = horizDot(nright, center);
	if (dot1 == 0) {
	    return true;
	}
	if (dot1 < 0) {
	    return false;
	}

	Vector nleft = new Vector(left.getZ(), left.getY(), -left.getX());
	double dot2 = horizDot(nleft, center);
	return dot2 <= 0;
    }

    public static Vector makeVector(float yaw) {
	return new Location(null, 0, 0, 0, yaw, 0).getDirection();
    }

    private static double horizDot(Vector a, Vector b) {
	return a.getX() * b.getX() + a.getZ() * b.getZ();
    }

    private BukkitUtils() {
    }
}
