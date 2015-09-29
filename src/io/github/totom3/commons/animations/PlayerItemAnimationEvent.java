package io.github.totom3.commons.animations;

import static com.google.common.base.Preconditions.checkNotNull;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Totom3
 */
public class PlayerItemAnimationEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ItemStack item;
    private final ItemAnimation animation;
    private final AnimationType type;
    private final int oldTimer;

    /**
     * Constructs a new {@code PlayerItemAnimationEvent} which represents a
     * starting animation, for the following arguments. The item is set to the
     * item the player is currently holding.
     * <p>
     * @param player    the player who triggered/cancelled the animation. Must
     *                  not be {@code null}
     * @param animation the animation triggered or cancelled by the item. Must
     *                  not be {@code null}.
     * <p>
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public PlayerItemAnimationEvent(Player player, ItemAnimation animation) {
	super(checkNotNull(player));
	this.item = player.getItemInHand();
	this.animation = checkNotNull(animation);
	this.type = AnimationType.START;
	this.oldTimer = 0;
    }

    /**
     * Constructs a new {@code PlayerItemAnimationEvent} which represents a
     * cancel animation, for the following arguments. The item is set to the
     * item the player is currently holding.
     * <p>
     * @param player    the player who triggered/cancelled the animation. Must
     *                  not be {@code null}
     * @param animation the animation triggered or cancelled by the item. Must
     *                  not be {@code null}.
     * @param oldTimer  the old timer value of the player. Must be greater than
     *                  0.
     * <p>
     * <p>
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public PlayerItemAnimationEvent(Player player, ItemAnimation animation, int oldTimer) {
	super(player);

	if (oldTimer <= 0) {
	    throw new IllegalArgumentException("Old timer must be greater than 0 for animation cancel event.");
	}

	this.item = player.getItemInHand();
	this.animation = checkNotNull(animation);
	this.type = AnimationType.CANCEL;
	this.oldTimer = oldTimer;
    }

    /**
     * Returns the item held by the player while performing the animation. This
     * method should be used over
     * {@link org.bukkit.entity.HumanEntity#getItemInHand()} or any other method
     * to get the currently held item, as it may have changed since the
     * animation was fired.
     * <p>
     * @return the held item, will never be {@code null}.
     */
    public ItemStack getItem() {
	return item;
    }

    public int getOldTimer() {
	return oldTimer;
    }

    /**
     * Returns the animation triggered or cancelled.
     * <p>
     * @return the animation.
     */
    public ItemAnimation getAnimation() {
	return animation;
    }

    /**
     * Returns the animation type.
     * <p>
     * @return the animation type.
     */
    public AnimationType getType() {
	return type;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }

}
