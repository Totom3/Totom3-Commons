package io.github.totom3.commons.meta;

import static com.google.common.base.Preconditions.checkNotNull;
import io.github.totom3.commons.CommonsMain;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Totom3
 */
public class EntitiesMetadataManager extends MetadataManager<Entity> {

    private static EntitiesMetadataManager instance;

    public synchronized static EntitiesMetadataManager get() {
	if (instance == null) {
	    instance = new EntitiesMetadataManager(CommonsMain.get());
	}
	return instance;
    }

    private final Plugin plugin;

    private EntitiesMetadataManager(Plugin plugin) {
	super(new WeakHashMap<>());
	this.plugin = checkNotNull(plugin);
    }

    public Plugin getPlugin() {
	return plugin;
    }

    public void enable() {
	Bukkit.getPluginManager().registerEvents(new MetadataListener(), plugin);
    }

    class MetadataListener implements Listener {

	@EventHandler
	void on(EntityDeathEvent event) {
	    if (event instanceof PlayerDeathEvent) {
		return;
	    }

	    removeAll(event.getEntity());
	}

	@EventHandler
	void on(PlayerQuitEvent event) {
	    removeAll(event.getPlayer());
	}

	@EventHandler
	void on(PlayerKickEvent event) {
	    removeAll(event.getPlayer());
	}

    }
}
