package io.github.totom3.commons.invmenus;

import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.CommonsMain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Totom3
 */
public abstract class InventoryMenu {

    private static final Map<Inventory, InventoryMenu> menusByInventories = new WeakHashMap<>();
    private static final Map<Player, InventoryMenu> menusByPlayers = new WeakHashMap<>();
    private static InventoriesListener list;

    /**
     * Returns the slot number representing the specified coordinates. The slot
     * is calculated by the following expression:
     * <pre>
     * slot = column + (row * 9)
     * </pre>
     * @param row    the row of the slot to get, starting at 0.
     * @param column the column of the slot to get, starting at 0.
     * <p>
     * @return the slot representing the intersection of the row and column
     *         passed as arguments.
     */
    public static int getSlot(int row, int column) {
	return column + (row * 9);
    }

    private static void ensureRegistered() {
	if (list != null) {
	    return;
	}
	list = new InventoriesListener();
	Bukkit.getPluginManager().registerEvents(list, CommonsMain.get());
    }

    private final int size;
    private final Player player;
    private final Map<Integer, MenuItem> items = new HashMap<>();

    private boolean isOpen;
    private Inventory inventory;
    private InventoryMenu parent;

    public InventoryMenu(Player player, int size) {
	this(player, size, null);
    }

    public InventoryMenu(Player player, int size, InventoryMenu parent) {
	this.size = size;
	this.player = checkNotNull(player);
	this.parent = parent;
    }

    private void checkSlot(int s) {
	if (s < 0) {
	    throw new IllegalArgumentException("slot cannot be negative");
	}

	if (s >= size) {
	    throw new IllegalArgumentException("slot must be smaller than size (" + size + ")");
	}
    }

    public boolean isOpen() {
	return isOpen;
    }

    public void open() {
	if (isOpen()) {
	    throw new IllegalStateException("menu is already open");
	}

	populate();
	isOpen = true;
	inventory = Bukkit.createInventory(null, size, getTitle());
	for (Entry<Integer, MenuItem> entry : items.entrySet()) {
	    int slot = entry.getKey();
	    MenuItem item = entry.getValue();
	    inventory.setItem(slot, item.stack);
	}

	menusByInventories.put(inventory, this);
	player.openInventory(inventory);
	menusByPlayers.put(player, this);

	ensureRegistered();
	onOpen();
    }

    public void close() {
	player.closeInventory();
	menusByInventories.remove(inventory);
	menusByPlayers.remove(player);
	isOpen = false;
	inventory = null;
	items.clear();

	onClose();

	if (list != null && menusByInventories.isEmpty() && menusByPlayers.isEmpty()) {
	    list.unregister();
	    list = null;
	}
    }

    public void refresh() {
	if (isOpen()) {
	    close();
	    open();
	}
    }

    public int getSize() {
	return size;
    }

    public Player getPlayer() {
	return player;
    }

    public Inventory getInventory() {
	if (!isOpen()) {
	    throw new IllegalStateException("menu is not open");
	}
	return inventory;
    }

    public InventoryMenu getParent() {
	return parent;
    }

    public boolean hasParent() {
	return parent != null;
    }

    public InventoryMenu openParent() {
	if (parent == null) {
	    throw new IllegalStateException("menu doesn't have a parent");
	}

	close();
	parent.open();
	return parent;
    }

    public abstract String getTitle();

    public InventoryMenu setItem(int slot, ClickHandler handler, ItemStack stack) {
	checkSlot(slot);
	MenuItem menuItem = new MenuItem(slot, stack, handler);
	items.put(slot, menuItem);

	if (isOpen()) {
	    inventory.setItem(slot, stack);
	}

	return this;
    }

    public InventoryMenu setItem(int slot, ClickHandler handler, Material mat, int data, String dispName, String... lore) {
	checkSlot(slot);
	ItemStack item = new ItemStack(mat, 1, (short) data);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(dispName);
	meta.setLore(Arrays.asList(lore));
	item.setItemMeta(meta);

	return setItem(slot, handler, item);
    }

    public InventoryMenu setItem(int slot, ClickHandler handler, Material mat, int data, String dispName, List<String> lore) {
	checkSlot(slot);
	ItemStack item = new ItemStack(mat, 1, (short) data);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(dispName);
	meta.setLore(lore);
	item.setItemMeta(meta);

	return setItem(slot, handler, item);
    }

    protected abstract void populate();

    protected abstract void onOpen();

    protected abstract void onClose();

    private static class InventoriesListener implements Listener {

	void unregister() {
	    HandlerList.unregisterAll(list);
	}

	@EventHandler
	void on(InventoryClickEvent event) {
	    Inventory inventory = event.getClickedInventory();
	    Player player = (Player) event.getWhoClicked();
	    int slot = event.getRawSlot();

	    InventoryMenu menu = menusByPlayers.get(player);
	    if (menu == null) {
		return;
	    }

	    event.setCancelled(true);

	    if (slot >= menu.getSize()) {
		return;
	    }

	    MenuItem menuItem = menu.items.get(slot);
	    if (menuItem == null) {
		return;
	    }

	    MenuClickEvent menuEvent = new MenuClickEvent(menuItem, player, menu, inventory);
	    if (menuItem.hasHandler()) {
		menuItem.getHandler().onClick(menuEvent);

		menuEvent.getBehavior().apply(menu);
	    }
	}

	@EventHandler
	void on(InventoryOpenEvent event) {
	    Player player = (Player) event.getPlayer();
	    Inventory inventory = event.getInventory();

	    InventoryMenu newMenu = menusByInventories.get(inventory);
	    InventoryMenu oldMenu = menusByPlayers.get(player);

	    if (newMenu != null && oldMenu != null && newMenu != oldMenu) {
		oldMenu.close();
	    }
	}

	@EventHandler
	void on(InventoryCloseEvent event) {
	    InventoryMenu menu = menusByInventories.remove(event.getInventory());
	    if (menu != null) {
		menusByPlayers.remove(menu.player);
		menu.close();
	    }
	}

	@EventHandler
	void on(PlayerQuitEvent event) {
	    closeInv(event.getPlayer());
	}

	@EventHandler
	void on(PlayerKickEvent event) {
	    closeInv(event.getPlayer());
	}

	private void closeInv(Player player) {
	    InventoryMenu menu = menusByPlayers.remove(player);
	    if (menu != null) {
		menusByInventories.remove(menu.inventory);
		menu.close();
	    }
	}
    }

}
