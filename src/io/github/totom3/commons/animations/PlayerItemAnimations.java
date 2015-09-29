package io.github.totom3.commons.animations;

import com.comphenix.packetwrapper.WrapperPlayClientBlockDig;
import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.protocol.PacketType;
import static com.comphenix.protocol.PacketType.Play.Client.BLOCK_DIG;
import static com.comphenix.protocol.PacketType.Play.Client.BLOCK_PLACE;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import io.github.totom3.commons.CommonsMain;
import io.github.totom3.commons.misc.TimerManager;
import java.lang.reflect.Field;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.DispenserRegistry;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EnumAnimation;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Totom3
 */
public class PlayerItemAnimations {

    private static final Field clickedItemField;

    private final static BiMap<EnumAnimation, ItemAnimation> nmsToCommons = EnumBiMap.create(EnumAnimation.class, ItemAnimation.class);
    private static PlayerItemAnimations instance;

    static {
	DispenserRegistry.c();

	nmsToCommons.put(EnumAnimation.NONE, ItemAnimation.NONE);
	nmsToCommons.put(EnumAnimation.EAT, ItemAnimation.EATING);
	nmsToCommons.put(EnumAnimation.DRINK, ItemAnimation.DRINKING);
	nmsToCommons.put(EnumAnimation.BLOCK, ItemAnimation.BLOCKING);
	nmsToCommons.put(EnumAnimation.BOW, ItemAnimation.PULLING);

	try {
	    clickedItemField = EntityHuman.class.getDeclaredField("g");
	    clickedItemField.setAccessible(true);
	} catch (NoSuchFieldException | SecurityException ex) {
	    throw new AssertionError("Could not initialize clicked item field", ex);
	}
    }

    public static ItemAnimation getAnimation(Material material) {
	Item item = CraftMagicNumbers.getItem(material);
	if (item == null) {
	    return ItemAnimation.NONE;
	}
	return nmsToCommons.get(item.e(null));
    }

    public synchronized static PlayerItemAnimations get() {
	if (instance == null) {
	    instance = new PlayerItemAnimations(CommonsMain.get());
	}

	return instance;
    }

    private final TimerManager<Player> timerManager = new TimerManager<>();
    private final Plugin plugin;
    private final Listener bukkitListener;
    private final PacketAdapter packetAdapter;

    private PlayerItemAnimations(Plugin plugin) {
	this.plugin = plugin;
	this.bukkitListener = new AnimationsListener();
	this.packetAdapter = new AnimationsAdapter(plugin);
    }

    public void enable() {
	Bukkit.getPluginManager().registerEvents(bukkitListener, plugin);
	ProtocolLibrary.getProtocolManager().addPacketListener(packetAdapter);
    }

    public void disable() {
	ProtocolLibrary.getProtocolManager().removePacketListener(packetAdapter);
    }

    public Plugin getPlugin() {
	return plugin;
    }

    public ItemAnimation getAnimation(Player player) {
	if (player == null) {
	    throw new NullPointerException();
	}

	if (!timerManager.hasTimer(player)) {
	    return ItemAnimation.NONE;
	}

	net.minecraft.server.v1_8_R3.ItemStack item = clickedItemInHand(player);
	if (item == null) {
	    stopTimer(player, ItemAnimation.NONE);
	    return ItemAnimation.NONE;
	}

	ItemAnimation animation = nmsToCommons.get(item.m());
	if (animation == ItemAnimation.NONE) {
	    stopTimer(player, ItemAnimation.NONE);
	    return ItemAnimation.NONE;
	}

	return animation;
    }

    public boolean hasAnimation(Player player, ItemAnimation animation) {
	checkNotNull(player);

	return getAnimation(player) == maskNull(animation);
    }

    public boolean isEating(Player player) {
	return getAnimation(checkNotNull(player)) == ItemAnimation.EATING;
    }

    public boolean isDrinking(Player player) {
	return getAnimation(checkNotNull(player)) == ItemAnimation.DRINKING;
    }

    public boolean isPullingBow(Player player) {
	return getAnimation(checkNotNull(player)) == ItemAnimation.PULLING;
    }

    public boolean isBlocking(Player player) {
	return getAnimation(checkNotNull(player)) == ItemAnimation.BLOCKING;
    }

    public int getAnimationDuration(Player player) {
	getAnimation(player); // clean entry
	return timerManager.getTimer(player);
    }

    private net.minecraft.server.v1_8_R3.ItemStack clickedItemInHand(Player player) {
	try {
	    return (net.minecraft.server.v1_8_R3.ItemStack) clickedItemField.get(((CraftPlayer) player).getHandle());
	} catch (IllegalArgumentException | IllegalAccessException ex) {
	    throw new AssertionError("could not get clicked item in hand field value for player " + player);
	}
    }

    private void startTimer(Player player, ItemAnimation animation) {
	Bukkit.getPluginManager().callEvent(new PlayerItemAnimationEvent(player, animation));
	timerManager.startTimer(player);
    }

    private void stopTimer(Player player, ItemAnimation animation) {
	int timer = timerManager.stopTimer(player);
	if (timer > 0) {
	    animation = maskNull(animation);
	    Bukkit.getPluginManager().callEvent(new PlayerItemAnimationEvent(player, animation, timer));
	}
    }

    private ItemAnimation maskNull(ItemAnimation anim) {
	if (anim == null) {
	    anim = ItemAnimation.NONE;
	}
	return anim;
    }

    class AnimationsAdapter extends PacketAdapter {

	AnimationsAdapter(Plugin plugin) {
	    super(plugin, BLOCK_DIG, BLOCK_PLACE);
	}

	@Override
	public void onPacketReceiving(PacketEvent pe) {
	    Player player = pe.getPlayer();
	    PacketContainer container = pe.getPacket();
	    PacketType type = pe.getPacketType();

	    if (type == BLOCK_PLACE) {	// start using item
		WrapperPlayClientBlockPlace packet = new WrapperPlayClientBlockPlace(container);
		ItemStack heldItem2 = packet.getHeldItem();
		Material material = (heldItem2 == null) ? Material.AIR : heldItem2.getType();
		ItemAnimation animation = getAnimation(material);
		if (animation != null) {
		    startTimer(player, animation);
		}
	    } else if (type == BLOCK_DIG) { // stop using item
		WrapperPlayClientBlockDig packet = new WrapperPlayClientBlockDig(container);
		if (packet.getStatus() != PlayerDigType.RELEASE_USE_ITEM) {
		    return;
		}

		Material material = player.getItemInHand().getType();

		ItemAnimation animation = getAnimation(material);
		if (animation == null) {
		    plugin.getLogger().log(Level.WARNING, "[Release Item] Found no animation for material {0}", material);
		    return;
		}

		stopTimer(player, animation);

	    }
	}

    }

    class AnimationsListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	void on(PlayerQuitEvent event) {
	    removeTimer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	void on(PlayerKickEvent event) {
	    removeTimer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void on(PlayerItemHeldEvent event) {
	    if (!event.isCancelled()) {
		removeTimer(event.getPlayer());
	    }
	}

	@EventHandler(priority = EventPriority.LOWEST)
	void on(PlayerDeathEvent event) {
	    removeTimer(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	void on(PlayerChangedWorldEvent event) {
	    removeTimer(event.getPlayer());
	}

	@EventHandler
	void on(InventoryClickEvent event) {
	    Inventory inv = event.getClickedInventory();
	    if (!(inv instanceof PlayerInventory)) {
		return;
	    }
	    PlayerInventory inventory = (PlayerInventory) inv;
	    Player player = (Player) inventory.getHolder();
	    if (player == null) {
		return;
	    }

	    ItemStack item = player.getItemInHand();
	    Bukkit.getScheduler().runTask(plugin, () -> {
		if (!player.getItemInHand().equals(item)) {
		    stopTimer(player, getAnimation(item.getType()));
		}
	    });
	}

	void removeTimer(Player player) {
	    stopTimer(player, getAnimation(player.getItemInHand().getType()));
	}
    }

}
