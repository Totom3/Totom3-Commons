package io.github.totom3.commons.npc;

import com.google.common.base.Preconditions;
import io.github.totom3.commons.bukkit.ImmutableLocation;
import java.util.Random;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Totom3
 */
public class IdleAI {

    private static final double DEFAULT_MAX_DISTANCE = 7; // 7 blocks
    private static final int DEFAULT_AVG_TICKS_BETWEEN_MOTION = 12; // 6 seconds

    private final int avgTicksBetweenMotion;
    private final double maxDistance;
    private final ImmutableLocation origin;
    private final Navigator navigator;
    private final Plugin plugin;
    private final NPCListener listener;

    private boolean started;
    private boolean interrupted;
    private BukkitTask tickerTask;

    public IdleAI(Plugin plugin, Navigator navigator) {
	this.listener = new NPCListener();
	Preconditions.checkNotNull(navigator, "Navigator cannot be null");
	Preconditions.checkNotNull(plugin, "Plugin cannot be null");
	
	this.plugin = plugin;
	this.navigator = navigator;
	this.maxDistance = DEFAULT_MAX_DISTANCE;
	this.origin = null;
	this.avgTicksBetweenMotion = DEFAULT_AVG_TICKS_BETWEEN_MOTION;
    }

    public IdleAI(Plugin plugin, Navigator navigator, double maxDistance) {
	this.listener = new NPCListener();
	Preconditions.checkArgument(maxDistance > 0, "Max distance must be greater than 0");
	Preconditions.checkNotNull(navigator, "Navigator cannot be null");
	Preconditions.checkNotNull(plugin, "Plugin cannot be null");

	this.maxDistance = maxDistance;
	this.plugin = plugin;
	this.origin = null;
	this.avgTicksBetweenMotion = DEFAULT_AVG_TICKS_BETWEEN_MOTION;
	this.navigator = navigator;
    }

    public IdleAI(Plugin plugin, Navigator navigator, int averageTicksBetweenMotion, double maxDistance) {
	this.listener = new NPCListener();
	Preconditions.checkArgument(averageTicksBetweenMotion >= 0, "Average ticks between motion cannot be negative");
	Preconditions.checkArgument(maxDistance > 0, "Max distance must be greater than 0");
	Preconditions.checkNotNull(navigator, "Navigator cannot be null");
	Preconditions.checkNotNull(plugin, "Plugin cannot be null");

	this.avgTicksBetweenMotion = averageTicksBetweenMotion;
	this.maxDistance = maxDistance;
	this.navigator = navigator;
	this.plugin = plugin;
	this.origin = null;
    }

    public IdleAI(Plugin plugin, Navigator navigator, int averageTicksBetweenMotion, Location origin, double maxDistance) {
	this.listener = new NPCListener();
	Preconditions.checkArgument(averageTicksBetweenMotion >= 0, "Average ticks between motion cannot be negative");
	Preconditions.checkArgument(maxDistance > 0, "Max distance must be greater than 0");
	Preconditions.checkNotNull(origin, "Origin cannot be null");
	Preconditions.checkNotNull(navigator, "Navigator cannot be null");
	Preconditions.checkNotNull(plugin, "Plugin cannot be null");

	this.plugin = plugin;
	this.avgTicksBetweenMotion = averageTicksBetweenMotion;
	this.maxDistance = maxDistance;
	this.origin = ImmutableLocation.of(origin);
	this.navigator = navigator;
    }

    public IdleAI(Plugin plugin, Navigator navigator, Location origin, double maxDistance) {
	this.listener = new NPCListener();
	Preconditions.checkArgument(maxDistance > 0, "Max distance must be greater than 0");
	Preconditions.checkNotNull(origin, "Origin cannot be null");
	Preconditions.checkNotNull(navigator, "Navigator cannot be null");
	Preconditions.checkNotNull(plugin, "Plugin cannot be null");

	this.avgTicksBetweenMotion = DEFAULT_AVG_TICKS_BETWEEN_MOTION;
	this.maxDistance = maxDistance;
	this.origin = ImmutableLocation.of(origin);
	this.navigator = navigator;
	this.plugin = plugin;
    }

    public Navigator getNavigator() {
	return navigator;
    }

    public NPC getNPC() {
	return navigator.getNPC();
    }

    public int getAvgTicksBetweenMotion() {
	return avgTicksBetweenMotion;
    }

    public boolean isBoundToOrigin() {
	return origin != null;
    }

    public double getMaxDistance() {
	return maxDistance;
    }

    public ImmutableLocation getOrigin() {
	return origin;
    }

    public Plugin getPlugin() {
	return plugin;
    }

    public void start() {
	if (started) {
	    throw new IllegalStateException("Idle AI was already started");
	}
	Bukkit.getPluginManager().registerEvents(listener, plugin);
	this.tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, new Ticker(), 0, 10);
	started = true;
	interrupted = false;
    }

    public void stop() {
	if (!started) {
	    throw new IllegalStateException("Idle AI was already stopped");
	}
	HandlerList.unregisterAll(listener);
	
	tickerTask.cancel();
	navigator.setTarget(null);
	tickerTask = null;
	started = false;
    }

    final class Ticker implements Runnable {

	final Random rand = new Random();

	@Override
	public void run() {
	    if (rand.nextInt(getAvgTicksBetweenMotion()) != 0) {
		return;
	    }

	    Location origin = (isBoundToOrigin()) ? getOrigin() : getNPC().getEntity().getLocation();
	    double maxDist = getMaxDistance();
	    Location loc = null;
	    int tryCount = 0;
	    do {
		float yaw = (float) Math.toRadians(rand.nextFloat() * 360);
		float distance = (float) (rand.nextFloat() * maxDist);

		loc = new Location(origin.getWorld(),
			origin.getX() + (Math.cos(yaw) * distance),
			origin.getY(),
			origin.getZ() + (Math.sin(yaw) * distance));
		
		int minY = Math.max(0, origin.getBlockY() - 10);
		int maxY = Math.min(255, minY + 20);
		int currentY = minY;
		
		do {
		    loc.setY(currentY);
		} while (!canStandAt(loc) & ++currentY <= maxY);
		
	    } while (!canStandAt(loc) & tryCount++ < 20);

	    getNavigator().setTarget(loc);
	}
	
	private boolean canStandAt(Location loc) {
	    Block block = loc.getBlock();
	    
	    if (block.getType().isSolid()) {
		return false;
	    }
	    
	    Block up = block.getRelative(BlockFace.UP);
	    if (up != null && up.getType().isSolid()) {
		return false;
	    }
	    
	    Block down = block.getRelative(BlockFace.DOWN);
	    
	    return !(down == null || !down.getType().isSolid());
	}
    }
    
    private class NPCListener implements Listener {
	
	@EventHandler
	void on(NPCDespawnEvent event) {
	    NPC npc = event.getNPC();
	    if (!npc.equals(navigator.getNPC())) {
		return;
	    }
	    
	    DespawnReason reason = event.getReason();
	    if (reason == DespawnReason.REMOVAL || reason == DespawnReason.PLUGIN) {
		return;
	    }
	    
	    
	    interrupted = true;
	    stop();
	}
	
	@EventHandler
	void on(NPCSpawnEvent event) {
	    NPC npc = event.getNPC();
	    if (!npc.equals(navigator.getNPC())) {
		return;
	    }
	    
	    if (interrupted) {
		start();
	    }
	}
    }
}
