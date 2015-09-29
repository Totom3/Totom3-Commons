package io.github.totom3.commons.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Totom3
 */
public abstract class PlayerSession {

    private static final Map<Player, PlayerSession> sessions = new WeakHashMap<>();

    private static long sessionCount = 0;

    private static String makeName(Player player) {
	return (sessionCount++) + "-" + player.getName();
    }

    private final SessionListener listener;
    private final SessionCommands commands;
    private final Player player;
    private final Plugin plugin;
    private final String name;
    private SessionState state;

    /**
     * Creates a new {@code PlayerSession} with the specified arguments. The
     * name will be automatically generated.
     * <p>
     * @param plugin the plugin that created this {@code PlayerSession}. Cannot
     *               be {@code null}.
     * @param player the player involved in this session. Cannot be
     *               {@code null}.
     */
    protected PlayerSession(Plugin plugin, Player player) {
	this.listener = new SessionListener();
	this.commands = new SessionCommands();
	this.state = SessionState.NOT_STARTED;
	this.player = checkNotNull(player);
	this.plugin = checkNotNull(plugin);
	this.name = makeName(player);
    }

    /**
     * Creates a new {@code PlayerSession} with the specified arguments.
     * <p>
     * @param plugin      the plugin that created this {@code PlayerSession}.
     *                    Cannot be {@code null}.
     * @param player      the player involved in this session. Cannot be
     *                    {@code null}.
     * @param sessionName the name of this session (used for debugging
     *                    purposes). May be {@code null} or empty, in which case
     *                    the name will be generated internally.
     */
    protected PlayerSession(Plugin plugin, Player player, String sessionName) {
	this.listener = new SessionListener();
	this.commands = new SessionCommands();
	this.state = SessionState.NOT_STARTED;
	this.player = checkNotNull(player);
	this.plugin = checkNotNull(plugin);
	this.name = (Strings.isNullOrEmpty(sessionName)) ? makeName(player) : sessionName;
    }

    // -----------------------------[ GETTERS ]-----------------------------
    /**
     * Returns the {@code Player} of this {@code PlayerSession}.
     * <p>
     * @return
     */
    public final Player getPlayer() {
	return player;
    }

    /**
     * Returns the current state of this {@code PlayerSession}.
     * <p>
     * @return
     */
    public final SessionState getSessionState() {
	return state;
    }

    /**
     * Returns the plugin associated with this {@code PlayerSession}.
     * <p>
     * @return
     */
    public final Plugin getPlugin() {
	return plugin;
    }

    // -----------------------------[ START/STOP MECHANICS ]-----------------------------
    /**
     * Starts this {@code PlayerSession}.
     * <p>
     * @throws IllegalStateException if the session was already started
     * <b>OR</b> if the player is not currently online <b>OR</b> if the plugin
     * associated with this {@code PlayerSession} is not enabled <b>OR</b> if
     * the underlying implementation refuses to start the session at the moment
     * this method is called, for any reason.
     * <p>
     * @throws RuntimeException      if an implementation-specific error occurs
     *                               while starting the session.
     */
    public final void start() throws IllegalStateException {
	if (state != SessionState.NOT_STARTED) {
	    state = SessionState.FAILED;
	    throw new IllegalStateException("Session was already started");
	}

	if (!player.isOnline()) {
	    throw new IllegalStateException("Player " + player.getName() + " is not online; cannot start session");
	}

	PlayerSession session = sessions.get(player);
	if (session != null) {    
	    session.stop();
	}

	sessions.put(player, this);

	registerCommandHandlers(commands);
	if (!plugin.isEnabled()) {
	    throw new IllegalStateException("Plugin is not enabled");
	}
	Bukkit.getPluginManager().registerEvents(listener, plugin);

	try {
	    onStart();
	    state = SessionState.STARTED;

	} catch (IllegalStateException ex) {
	    internalTerminate(SessionState.FAILED);
	    throw new IllegalStateException("Could not start session " + name, ex);

	} catch (RuntimeException ex) {
	    internalTerminate(SessionState.FAILED);
	    throw new RuntimeException("Could not start session " + name, ex);
	}

	sessions.put(player, this);
    }

    /**
     * Stops this {@code PlayerSession} if it is currently running. If it is
     * not, this method does nothing. Implementations might choose to deny the
     * stop by throwing an {@code IllegalStateException}, in which case the
     * exception will be re-thrown.
     * <p>
     * @throws IllegalStateException if this {@code PlayerSession} cannot be
     *                               stopped at the moment this method is
     *                               called.
     * @throws RuntimeException      if an implementation-specific error occurs
     *                               while stopping the session.
     */
    public final void stop() throws IllegalStateException {
	if (state != SessionState.STARTED) {
	    return;
	}

	sessions.remove(player);

	try {
	    onStop();
	    internalTerminate(SessionState.ENDED);
	} catch (IllegalStateException ex) {
	    throw ex;

	} catch (RuntimeException ex) {
	    internalTerminate(SessionState.FAILED);
	    throw new RuntimeException("Could not stop session " + name, ex);
	}
    }

    /**
     * Reserved for implementations to choose to act when this
     * {@code PlayerSession} is started.
     * <p>
     * Note: implementations must never explicitely call this method. Use
     * {@link #start()} instead.
     * </p>
     */
    protected abstract void onStart() throws IllegalStateException;

    /**
     * Reserved for implementations to choose to act when this
     * {@code PlayerSession} is being stopped. It is possible for an
     * implementation to deny the stop by throwing an
     * {@code IllegalStateException}, in which case the session will still be
     * running and will function normally.
     * <p>
     * Note: implementations must never explicitely call this method. Use
     * {@link #stop()} instead.
     * </p>
     * <p>
     * @throws IllegalStateException if the session cannot be stopped at this
     *                               point.
     */
    protected abstract void onStop() throws IllegalStateException;

    private void internalTerminate(SessionState state) {
	this.commands.clear();
	this.state = state;
	HandlerList.unregisterAll(listener);
    }

    /**
     * Registers the session commands. Implementations should <em>never</em>
     * call this method directly.
     * <p>
     * @param cmds the {@code SessionCommands} to register the commands at.
     */
    protected abstract void registerCommandHandlers(SessionCommands cmds);

    @FunctionalInterface
    public static interface SessionCommandHandler {

	void handle(String[] args);
    }

    public static class SessionCommands {

	Map<String, SessionCommandHandler> map = new HashMap<>(10);

	public SessionCommandHandler get(String name) {
	    return map.get(name.toLowerCase());
	}

	public SessionCommandHandler remove(String name) {
	    return map.remove(name.toLowerCase());
	}

	public SessionCommandHandler set(String name, SessionCommandHandler cmd) {
	    if (cmd == null) {
		return remove(name);
	    } else {
		return map.put(name.toLowerCase(), cmd);
	    }
	}

	public Set<String> keys() {
	    return Collections.unmodifiableSet(map.keySet());
	}

	public Collection<SessionCommandHandler> handlers() {
	    return Collections.unmodifiableCollection(map.values());
	}

	public Set<Entry<String, SessionCommandHandler>> all() {
	    return Collections.unmodifiableSet(map.entrySet());
	}

	public boolean contains(String key) {
	    return get(key) != null;
	}

	public void clear() {
	    map.clear();
	}
    }

    public static enum SessionState {

	NOT_STARTED,
	STARTED,
	ENDED,
	FAILED
    }

    private class SessionListener implements Listener {

	@EventHandler
	void on(AsyncPlayerChatEvent event) {
	    if (!event.getPlayer().equals(player)) {
		return;
	    }

	    event.setCancelled(true);

	    String message = event.getMessage();
	    String[] args = StringUtils.split(message, ' ');
	    SessionCommandHandler handler = commands.get(args[0]);
	    if (handler == null) {
		player.sendMessage(ChatColor.DARK_RED + "Available commands: " + ChatColor.RED + commands.keys());
		return;
	    }

	    Runnable run = () -> {
		try {
		    handler.handle(Arrays.copyOfRange(args, 1, args.length));
		} catch (RuntimeException ex) {
		    internalTerminate(SessionState.FAILED);
		    throw new RuntimeException("Could not handle command " + args[0] + " in session " + name, ex);
		}
	    };

	    if (!event.isAsynchronous()) {
		run.run();
	    } else {
		Bukkit.getScheduler().runTask(plugin, run);
	    }
	}

	@EventHandler
	void on(PlayerQuitEvent event) {
	    onQuit(event);
	}

	@EventHandler
	void on(PlayerKickEvent event) {
	    onQuit(event);
	}

	@EventHandler
	void on(PluginDisableEvent event) {
	    if (event.getPlugin().equals(plugin)) {
		stop();
	    }
	}

	void onQuit(PlayerEvent event) {
	    if (event.getPlayer().equals(player)) {
		stop();
	    }
	}
    }
}
