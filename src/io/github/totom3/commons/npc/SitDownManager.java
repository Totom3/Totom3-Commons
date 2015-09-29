package io.github.totom3.commons.npc;

import com.comphenix.packetwrapper.WrapperPlayServerAttachEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.CommonsMain;
import io.github.totom3.commons.BukkitUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Totom3
 */
public class SitDownManager {

    private static SitDownManager instance;

    private static final PacketType SPAWN_NPC_TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;
    private static final PacketType DESTROY_TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    private static final EntityChicken sharedVehicle = new EntityChicken(null);
    private static final List<WrappedWatchableObject> metadata
	    = Arrays.asList(
		    new WrappedWatchableObject(0, (byte) 0x20),
		    new WrappedWatchableObject(15, (byte) 1));
    private static final Field entityCountField;
    private static final EntityType VEHICLE_TYPE = EntityType.CHICKEN;

    static {
	try {
	    entityCountField = Entity.class.getDeclaredField("entityCount");
	    entityCountField.setAccessible(true);
	} catch (NoSuchFieldException | SecurityException ex) {
	    throw new RuntimeException("Could not get 'entityCount' field from Entity class", ex);
	}
    }

    public static SitDownManager get() {
	if (instance == null) {
	    instance = new SitDownManager(CommonsMain.get());
	}
	return instance;
    }

    private final Map<NPC, Integer> sitting = new HashMap<>();
    private PacketListener spawnPacketListener;
    private final Plugin plugin;

    private SitDownManager(Plugin plugin) {
	this.plugin = checkNotNull(plugin);
    }

    public void enable() {
	spawnPacketListener = new NPCPacketAdapter();
	ProtocolLibrary.getProtocolManager().addPacketListener(spawnPacketListener);
	Bukkit.getPluginManager().registerEvents(new NPCListener(), plugin);
    }

    public void disable() {
	ProtocolLibrary.getProtocolManager().removePacketListener(spawnPacketListener);
    }

    public Plugin getPlugin() {
	return plugin;
    }

    public Set<NPC> getAll() {
	return Collections.unmodifiableSet(sitting.keySet());
    }

    public boolean isSitting(NPC npc) {
	return isTechnicallySitting(npc) || isActuallySitting(npc);
    }

    private boolean isActuallySitting(NPC npc) {
	return sitting.containsKey(npc);
    }

    private boolean isTechnicallySitting(NPC npc) {
	return npc.hasTrait(NPCPoseTrait.class) && npc.getTrait(NPCPoseTrait.class).isSitting();
    }

    public boolean sitDown(NPC npc) {
	checkNotNull(npc);
	if (!npc.isSpawned()) {
	    throw new IllegalArgumentException("NPC " + npc.getName() + " is not spawned");
	}
	if (isActuallySitting(npc)) {
	    return false;
	}
	org.bukkit.entity.Entity entity = npc.getEntity();

	int vehicleID = makeVehicleID();

	sitting.put(npc, vehicleID);

	getTrait(npc).setSitting();

	List<Player> trackers = ProtocolLibrary.getProtocolManager().getEntityTrackers(entity);
	sendSitDown(trackers, npc, vehicleID);

	return true;
    }

    private NPCPoseTrait getTrait(NPC npc) {
	if (!npc.hasTrait(NPCPoseTrait.class)) {
	    npc.addTrait(NPCPoseTrait.class);
	}
	NPCPoseTrait trait = npc.getTrait(NPCPoseTrait.class);
	return trait;
    }

    public boolean standUp(NPC npc) {
	checkNotNull(npc);
	if (!npc.isSpawned()) {
	    throw new IllegalArgumentException("NPC " + npc.getName() + " is not spawned");
	}
	if (!isActuallySitting(npc)) {
	    return false;
	}

	getTrait(npc).setStanding();

	org.bukkit.entity.Entity entity = npc.getEntity();

	int vehicleID = sitting.remove(npc);

	List<Player> trackers = ProtocolLibrary.getProtocolManager().getEntityTrackers(entity);
	sendStandUp(trackers, vehicleID);

	return true;
    }

    public void standUpAll() {
	sitting.keySet().forEach(this::standUp);
    }

    private void sendSitDown(Iterable<? extends Player> itr, NPC npc, int vehicleID) {
	PacketContainer spawnPacket = makeSpawnPacket(vehicleID, npc.getStoredLocation());
	PacketContainer metaPacket = makeMetadataPacket(vehicleID);
	PacketContainer sitPacket = makeSitPacket(npc.getEntity().getEntityId(), vehicleID);
	PacketContainer lookPacket = makeLookPacket(npc, vehicleID);

	ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	for (Player p : itr) {
	    try {
		pm.sendServerPacket(p, spawnPacket, false);
		pm.sendServerPacket(p, metaPacket, false);
		pm.sendServerPacket(p, lookPacket, false);
		pm.sendServerPacket(p, sitPacket, false);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    }
	}
    }

    private void scheduleSitDown(PacketEvent pe, Player p, NPC npc, int vehicleID) {
	PacketContainer spawnPacket = makeSpawnPacket(vehicleID, npc.getStoredLocation());
	PacketContainer metaPacket = makeMetadataPacket(vehicleID);
	PacketContainer lookPacket = makeLookPacket(npc, vehicleID);
	PacketContainer sitPacket = makeSitPacket(npc.getEntity().getEntityId(), vehicleID);

	pe.schedule(ScheduledPacket.fromSilent(spawnPacket, p));
	pe.schedule(ScheduledPacket.fromSilent(metaPacket, p));
	pe.schedule(ScheduledPacket.fromSilent(lookPacket, p));
	pe.schedule(ScheduledPacket.fromSilent(sitPacket, p));
    }

    private void sendStandUp(Iterable<? extends Player> itr, int vehicleID) {
	PacketContainer destroyPacket = makeDestroyPacket(vehicleID);
	PacketContainer standUpPacket = makeStandUpPacket(vehicleID);

	ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	for (Player p : itr) {
	    try {
		pm.sendServerPacket(p, standUpPacket, false);
		pm.sendServerPacket(p, destroyPacket, false);
	    } catch (InvocationTargetException ex) {
		throw new RuntimeException(ex);
	    }
	}
    }

    private void scheduleStandUp(PacketEvent pe, Player p, int vehicleID) {
	PacketContainer destroyPacket = makeDestroyPacket(vehicleID);
	PacketContainer standUpPacket = makeStandUpPacket(vehicleID);

	pe.schedule(ScheduledPacket.fromSilent(standUpPacket, p));
	pe.schedule(ScheduledPacket.fromSilent(destroyPacket, p));
    }

    private PacketContainer makeSpawnPacket(int id, Location loc) {
	sharedVehicle.setLocation(loc.getX(), loc.getY() - 0.45, loc.getZ(), loc.getPitch(), loc.getYaw());
	PacketContainer cont = new PacketContainer(
		PacketType.Play.Server.SPAWN_ENTITY_LIVING,
		new PacketPlayOutSpawnEntityLiving(sharedVehicle));
	WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving(cont);
	wrapper.setEntityID(id);
	wrapper.setType(EntityType.CHICKEN);
	return cont;
    }

    private PacketContainer makeMetadataPacket(int id) {
	WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata();
	wrapper.setEntityID(id);
	wrapper.setMetadata(metadata);
	return wrapper.getHandle();
    }

    private PacketContainer makeSitPacket(int npcID, int vehicleID) {
	WrapperPlayServerAttachEntity wrapper = new WrapperPlayServerAttachEntity();
	wrapper.setLeash(false);
	wrapper.setEntityID(npcID);
	wrapper.setVehicleId(vehicleID);
	return wrapper.getHandle();
    }

    private PacketContainer makeLookPacket(NPC npc, int vehicleID) {
	Location loc = ((LivingEntity) npc.getEntity()).getEyeLocation();
	byte yaw = (byte) (int) (loc.getYaw() * 256.0F / 360.0F);
	WrapperPlayServerEntityHeadRotation wrapper = new WrapperPlayServerEntityHeadRotation();
	wrapper.setEntityID(vehicleID);
	wrapper.setHeadYaw(yaw);
	return wrapper.getHandle();
    }

    private PacketContainer makeDestroyPacket(int id) {
	return new PacketContainer(DESTROY_TYPE, new PacketPlayOutEntityDestroy(id));
    }

    private PacketContainer makeStandUpPacket(int vehicleID) {
	WrapperPlayServerAttachEntity wrapper = new WrapperPlayServerAttachEntity();
	wrapper.setLeash(false);
	wrapper.setEntityID(-1);
	wrapper.setVehicleId(vehicleID);
	return wrapper.getHandle();
    }

    private int makeVehicleID() {
	try {
	    int count = entityCountField.getInt(null);
	    entityCountField.setInt(null, count + 1);
	    return count;
	} catch (IllegalAccessException | IllegalArgumentException ex) {
	    throw new AssertionError("Could not get 'entityCount' field value", ex);
	}
    }

    class NPCListener implements Listener {

	@EventHandler
	void on(NPCDespawnEvent event) {
	    handleDespawn(event.getNPC());
	}

	void handleDespawn(NPC npc) {
	    if (!isSitting(npc)) {
		return;
	    }
	    NPCPoseTrait trait = npc.getTrait(NPCPoseTrait.class);
	    trait.setSitting();
	}

	@EventHandler
	void on(net.citizensnpcs.api.event.NPCRemoveEvent event) {
	    handleDespawn(event.getNPC());
	}

	@EventHandler
	void on(NPCSpawnEvent event) {
	    NPC npc = event.getNPC();
	    if (!npc.hasTrait(NPCPoseTrait.class)) {
		return;
	    }

	    if (npc.getTrait(NPCPoseTrait.class).isSitting()) {
		sitDown(npc);
	    }
	}

    }

    class NPCPacketAdapter extends PacketAdapter {

	NPCPacketAdapter() {
	    super(SitDownManager.this.plugin, SPAWN_NPC_TYPE);
	}

	@Override
	public void onPacketSending(PacketEvent pe) {
	    Player player = pe.getPlayer();
	    PacketContainer packet = pe.getPacket();
	    PacketType type = pe.getPacketType();

	    if (type == SPAWN_NPC_TYPE) {
		org.bukkit.entity.Entity npcEntity = BukkitUtils.getEntity(packet.getIntegers().read(0), player.getWorld());
		if (npcEntity == null) {
		    return;
		}

		NPC npc = CitizensAPI.getNPCRegistry().getNPC(npcEntity);
		if (npc == null) {
		    return;
		}

		if (isSitting(npc)) {
		    scheduleSitDown(pe, player, npc, sitting.get(npc));
		}

	    } else if (type == DESTROY_TYPE) {
		org.bukkit.entity.Entity npcEntity = BukkitUtils.getEntity(packet.getIntegers().read(0), player.getWorld());
		if (npcEntity == null) {
		    return;
		}

		NPC npc = CitizensAPI.getNPCRegistry().getNPC(npcEntity);
		if (npc == null) {
		    return;
		}

		Integer id = sitting.get(npc);
		if (id != null) {
		    scheduleStandUp(pe, player, id);
		}

	    }
	}
    }
}
