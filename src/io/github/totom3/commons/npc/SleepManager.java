package io.github.totom3.commons.npc;

import com.bergerkiller.bukkit.common.utils.FaceUtils;
import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.wrappers.BlockPosition;
import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.CommonsMain;
import io.github.totom3.commons.BukkitUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Totom3
 */
public class SleepManager {

    private static final EnumMap<BlockFace, Integer> DATAS = new EnumMap<>(BlockFace.class);

    private static final int LEAVE_BED_ANIMATION = 2;
    private static final PacketType SPAWN_TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;
    private static final int BED_Y = 2;
    private static SleepManager instance;

    static {
	DATAS.put(BlockFace.EAST, 8);
	DATAS.put(BlockFace.SOUTH, 9);
	DATAS.put(BlockFace.WEST, 10);
	DATAS.put(BlockFace.NORTH, 11);
    }

    public static SleepManager get() {
	if (instance == null) {
	   instance = new SleepManager(CommonsMain.get());
	}
	return instance;
    }

    private ProtocolManager manager;

    private final Plugin thePlugin;
    private final BedPacketAdapter adapter;
    private final Listener listener;

    private final Set<NPC> sleeping = new HashSet<>();

    private SleepManager(Plugin plugin) {

	this.thePlugin = checkNotNull(plugin);
	this.adapter = new BedPacketAdapter();
	this.listener = new Listener() {

	    @EventHandler
	    private void on(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof HumanEntity) {
		    NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		    if (npc != null) {
			onLeaveOrDeath(npc, (HumanEntity) entity);
		    }
		}
	    }

	    private void onLeaveOrDeath(NPC npc, HumanEntity entity) {
		if (npc.isSpawned()) {
		    wakeUp(npc);
		} else {
		    sleeping.remove(npc);
		    WrapperPlayServerAnimation packet = makeWakeupPacket(entity);
		    List<Player> trackers = manager.getEntityTrackers(entity);
		    trackers.forEach(packet::sendPacket);
		}
	    }
	};
    }

    public void enable() {

	// import org.bukkit.craftbukkit.v1_8_R3.event.Test;
	manager = ProtocolLibrary.getProtocolManager();

	Bukkit.getPluginManager().registerEvents(listener, thePlugin);
	manager.addPacketListener(adapter);
    }

    public void disable() {
	wakeAll();
	manager.removePacketListener(adapter);
	HandlerList.unregisterAll(listener);
    }

    public boolean isSleeping(NPC npc) {
	return sleeping.contains(npc);
    }

    public void setSleeping(NPC npc, boolean sleep) {
	checkNPC(npc);
	if (sleep) {
	    sleep(npc);
	} else {
	    wakeUp(npc);
	}
    }

    public boolean sleep(NPC npc) {
	checkNPC(npc);

	getTrait(npc).setSleeping();
	Entity entity = npc.getEntity();
	if (!sleeping.add(npc)) {
	    return false;
	}

	Location loc = entity.getLocation();

	PacketContainer bedPacket = makeSleepPacket(entity).getHandle();
	PacketContainer blockChangePacket = makeBlockChangePacket(loc);

	List<Player> trackers = manager.getEntityTrackers(entity);
	ProtocolManager pm = ProtocolLibrary.getProtocolManager();
	for (Player p : trackers) {
	    try {
		pm.sendServerPacket(p, blockChangePacket, false);
		pm.sendServerPacket(p, bedPacket, false);
	    } catch (InvocationTargetException ex) {
		throw new AssertionError("Could not send sleep/block change packets to player " + p.getName(), ex);
	    }
	}

	return true;
    }

    private NPCPoseTrait getTrait(NPC npc) {
	if (!npc.hasTrait(NPCPoseTrait.class)) {
	    npc.addTrait(NPCPoseTrait.class);
	}
	NPCPoseTrait trait = npc.getTrait(NPCPoseTrait.class);
	return trait;
    }

    public boolean wakeUp(NPC npc) {
	checkNPC(npc);

	Entity entity = npc.getEntity();
	if (!sleeping.remove(npc)) {
	    return false;
	}

	WrapperPlayServerAnimation packet = makeWakeupPacket(entity);
	List<Player> trackers = manager.getEntityTrackers(entity);
	trackers.forEach(packet::sendPacket);
	return true;
    }

    private void checkNPC(NPC npc) throws IllegalArgumentException {
	if (npc == null) {
	    throw new NullPointerException("npc cannot be null");
	}
	if (!npc.isSpawned()) {
	    throw new IllegalArgumentException("npc is not spawned");
	}
    }

    private PacketContainer makeBlockChangePacket(Location loc) {
	return makeBlockChangePacket(((CraftWorld) loc.getWorld()).getHandle(), loc.getYaw(), loc.getBlockX(), BED_Y, loc.getBlockZ());
    }

    private PacketContainer makeBlockChangePacket(World world, float yaw, int x, int y, int z) {
	PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(world, new net.minecraft.server.v1_8_R3.BlockPosition(x, y, z));
	packet.block = CraftMagicNumbers.getBlock(Material.BED_BLOCK).fromLegacyData(DATAS.get(FaceUtils.yawToFace(yaw, false)));
	return new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE, packet);
    }

    public void wakeAll() {
	try {
	    for (NPC npc : sleeping) {
		if (!npc.isSpawned()) {
		    continue;
		}
		manager.broadcastServerPacket(makeWakeupPacket(npc.getEntity().getEntityId()).getHandle());
	    }
	} finally {
	    sleeping.clear();
	}
    }

    private WrapperPlayServerBed makeSleepPacket(Entity entity) {
	Location loc = entity.getLocation();
	return makeSleepPacket(entity.getEntityId(), loc.getBlockX(), BED_Y, loc.getBlockZ());
    }

    private WrapperPlayServerBed makeSleepPacket(int id, int x, int y, int z) {
	return makeSleepPacket(id, new BlockPosition(x, y, z));
    }

    private WrapperPlayServerBed makeSleepPacket(int id, BlockPosition pos) {
	WrapperPlayServerBed wrapper = new WrapperPlayServerBed();
	wrapper.setEntityID(id);
	wrapper.setLocation(pos);
	return wrapper;
    }

    private WrapperPlayServerAnimation makeWakeupPacket(Entity entity) {
	return makeWakeupPacket(entity.getEntityId());
    }

    private WrapperPlayServerAnimation makeWakeupPacket(int id) {
	WrapperPlayServerAnimation wrapper = new WrapperPlayServerAnimation();
	wrapper.setEntityID(id);
	wrapper.setAnimation(LEAVE_BED_ANIMATION);
	return wrapper;
    }

    private class BedPacketAdapter extends PacketAdapter {

	private BedPacketAdapter() {
	    super(thePlugin, SPAWN_TYPE);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
	    WrapperPlayServerNamedEntitySpawn wrapper = new WrapperPlayServerNamedEntitySpawn(event.getPacket());

	    Player player = event.getPlayer();

	    int id = wrapper.getEntityID();
	    Entity entity = BukkitUtils.getEntity(id, player.getWorld());
	    if (entity == null) {
		return;
	    }

	    NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
	    if (!sleeping.contains(npc)) {
		return;
	    }

	    BlockPosition pos = new BlockPosition((int) wrapper.getX(), BED_Y, (int) wrapper.getZ());

	    PacketContainer blockChangePacket = makeBlockChangePacket(((CraftWorld) player.getWorld()).getHandle(), wrapper.getYaw(), pos.getX(), BED_Y, pos.getZ());
	    PacketContainer sleepPacket = makeSleepPacket(wrapper.getEntityID(), pos).getHandle();

	    event.schedule(ScheduledPacket.fromSilent(blockChangePacket, player));
	    event.schedule(ScheduledPacket.fromSilent(sleepPacket, player));
	}
    }
}
