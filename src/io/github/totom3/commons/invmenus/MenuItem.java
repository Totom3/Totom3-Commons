package io.github.totom3.commons.invmenus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Totom3
 */
public class MenuItem {
    private final int slot;
    final ItemStack stack;
    private final ClickHandler handler;

    public MenuItem(int slot, ItemStack stack, ClickHandler handler) {
	if (stack == null || stack.getType() == Material.AIR) {
	    throw new IllegalArgumentException("stack cannot be null nor empty");
	}
	this.slot = slot;
	this.stack = stack;
	this.handler = (handler != null) ? handler : ClickHandler.NONE;
    }

    public int getSlot() {
	return slot;
    }

    public ItemStack getStack() {
	return stack.clone();
    }

    public boolean hasHandler() {
	return handler != null;
    }

    public ClickHandler getHandler() {
	return handler;
    }

}
