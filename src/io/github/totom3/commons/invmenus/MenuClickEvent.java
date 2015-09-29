package io.github.totom3.commons.invmenus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Totom3
 */
public class MenuClickEvent {

    private final Player player;
    private final MenuItem menuItem;
    private final InventoryMenu menu;
    private final Inventory inventory;
    private PostClickBehavior behavior = PostClickBehavior.NONE;

    public MenuClickEvent(MenuItem menuItem, Player player, InventoryMenu menu, Inventory inventory) {
	this.menuItem = menuItem;
	this.player = player;
	this.menu = menu;
	this.inventory = inventory;
    }

    public Inventory getInventory() {
	return inventory;
    }

    public MenuItem getMenuItem() {
	return menuItem;
    }

    public <M extends InventoryMenu> M getMenu() {
	return (M) menu;
    }

    public Player getPlayer() {
	return player;
    }

    public PostClickBehavior getBehavior() {
	return behavior;
    }

    public void setBehavior(PostClickBehavior behavior) {
	this.behavior = (behavior == null) ? PostClickBehavior.NONE : behavior;
    }

}
