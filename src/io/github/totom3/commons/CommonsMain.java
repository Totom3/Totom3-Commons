package io.github.totom3.commons;

import io.github.totom3.commons.animations.PlayerItemAnimations;
import io.github.totom3.commons.meta.EntitiesMetadataManager;
import io.github.totom3.commons.npc.SitDownManager;
import io.github.totom3.commons.npc.SleepManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Used so that the library can be recognized as a plugin by {@code Bukkit}. By
 * doing this, multiple plugins can use this, without the need to include the
 * classes in each one's .jar file.
 * <p>
 * @author Totom3
 */
public class CommonsMain extends JavaPlugin {

    private static CommonsMain instance;

    public static CommonsMain get() {
	return instance;
    }

    private SleepManager sleepManager;
    private SitDownManager sitDownManager;
    private EntitiesMetadataManager metaManager;
    private PlayerItemAnimations itemAnimations;

    public CommonsMain() {
	instance = this;
    }

    @Override
    public void onEnable() {
	sleepManager = SleepManager.get();
	sitDownManager = SitDownManager.get();
	metaManager = EntitiesMetadataManager.get();
	itemAnimations = PlayerItemAnimations.get();

	metaManager.enable();
	sleepManager.enable();
	sitDownManager.enable();
	itemAnimations.enable();
    }

    @Override
    public void onDisable() {
	sleepManager.disable();
	itemAnimations.disable();
	sitDownManager.disable();
    }

}
