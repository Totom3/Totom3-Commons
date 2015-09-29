package io.github.totom3.commons.entity;

import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.ControllerJump;
import net.minecraft.server.v1_8_R3.ControllerLook;
import net.minecraft.server.v1_8_R3.ControllerMove;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;

/**
 *
 * @author Totom3
 */
public final class EntityControllers {

    private static final Field controllerMoveField;
    private static final Field controllerLookField;
    private static final Field controllerJumpField;

    static {
	Class<EntityInsentient> clazz = EntityInsentient.class;
	try {
	    controllerMoveField = clazz.getDeclaredField("moveController");
	    controllerLookField = clazz.getDeclaredField("lookController");
	    controllerJumpField = clazz.getDeclaredField("g");

	    controllerMoveField.setAccessible(true);
	    controllerLookField.setAccessible(true);
	    controllerJumpField.setAccessible(true);
	} catch (NoSuchFieldException | SecurityException ex) {
	    throw new AssertionError("Could not initialize controller fields", ex);
	}
    }

    /**
     * Returns the {@code ControllerMove} of a {@code Creature}.
     *
     * @param creature the creature to get the move controller of.
     *
     * @return
     */
    public static ControllerMove getMoveController(Creature creature) {
	return getHandle(creature).getControllerMove();
    }

    /**
     * Returns the {@code ControllerLook} of a {@code Creature}.
     *
     * @param creature the creature to get the look controller of.
     *
     * @return
     */
    public static ControllerLook getLookController(Creature creature) {
	return getHandle(creature).getControllerLook();
    }

    /**
     * Returns the {@code ControllerJump} of a {@code Creature}.
     *
     * @param creature the creature to get the jump controller of.
     *
     * @return
     */
    public static ControllerJump getJumpController(Creature creature) {
	return getHandle(creature).getControllerJump();
    }

    /**
     * Changes the move controller of a {@code Creature} to a new
     * {@code FreezedControllerMove} which is by default freezed.
     *
     * @param creature the creature to set the move controller of.
     *
     * @return the newly created {@code FreezedControllerMove}.
     */
    public static FreezedControllerMove freezeMove(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	FreezedControllerMove controller = new FreezedControllerMove(handle, true);

	setField(controllerMoveField, handle, controller);

	return controller;
    }

    /**
     * Changes the look controller of a {@code Creature} to a new
     * {@code FreezedControllerLook} which is by default freezed.
     *
     * @param creature the creature to set the look controller of.
     *
     * @return the newly created {@code FreezedControllerLook}.
     */
    public static FreezedControllerLook freezeLook(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	FreezedControllerLook controller = new FreezedControllerLook(handle, true);

	setField(controllerLookField, handle, controller);

	return controller;
    }

    /**
     * Changes the jump controller of a {@code Creature} to a new
     * {@code FreezedControllerJump} which is by default freezed.
     *
     * @param creature the creature to set the jump controller of.
     *
     * @return the newly created {@code FreezedControllerJump}.
     */
    public static FreezedControllerJump freezeJump(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	FreezedControllerJump controller = new FreezedControllerJump(handle, true);

	setField(controllerJumpField, handle, controller);

	return controller;
    }

    /**
     * Resets the move controller of a {@code Creature}.
     *
     * @param creature the creature to reset the controller of.
     *
     * @return the newly created {@code ControllerMove}.
     */
    public static ControllerMove resetMove(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	ControllerMove controller = new ControllerMove(handle);

	setField(controllerMoveField, handle, controller);

	return controller;
    }

    /**
     * Resets the look controller of a {@code Creature}.
     *
     * @param creature the creature to reset the controller of.
     *
     * @return the newly created {@code ControllerLook}.
     */
    public static ControllerLook resetLook(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	ControllerLook controller = new ControllerLook(handle);

	setField(controllerLookField, handle, controller);

	return controller;
    }

    /**
     * Resets the jump controller of a {@code Creature}.
     *
     * @param creature the creature to reset the controller of.
     *
     * @return the newly created {@code ControllerJump}.
     */
    public static ControllerJump resetJump(Creature creature) {
	EntityInsentient handle = getHandle(creature);
	ControllerJump controller = new ControllerJump(handle);

	setField(controllerJumpField, handle, controller);

	return controller;
    }

    private static EntityInsentient getHandle(Creature creature) {
	if (creature == null) {
	    throw new NullPointerException("Creature cannot be null");
	}

	return ((CraftCreature) creature).getHandle();
    }

    private static void setField(Field f, Object obj, Object val) {
	try {
	    f.set(obj, val);
	} catch (IllegalArgumentException | IllegalAccessException ex) {
	    throw new AssertionError("Could not set value of field " + f.getName() + " on object " + obj + " to: " + val, ex);
	}
    }

    private EntityControllers() {
    }
}
