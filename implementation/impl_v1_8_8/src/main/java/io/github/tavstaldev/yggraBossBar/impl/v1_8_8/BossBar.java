package io.github.tavstaldev.yggraBossBar.impl.v1_8_8;

import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.impl.v1_8_8.models.EntityBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

/**
 * Boss bar implementation for Minecraft 1.8 through 1.9.3.
 * <p>
 * These versions have no boss bar API, so a client-side wither entity is spawned for every
 * viewer and its health is used to display the progress. Bars are refreshed periodically and
 * when players change worlds; viewers are cleaned up when they disconnect.
 */
@SuppressWarnings("unused")
public class BossBar implements IBossBar, Listener {
    /** Bars managed by this implementation, keyed by caller-chosen identifier. */
    private final HashMap<String, EntityBar> bossBars = new HashMap<>();

    /**
     * Creates the implementation and registers the listeners and refresh task required by
     * the entity-based approach.
     *
     * @param plugin the owning plugin used for event registration and scheduling
     */
    public BossBar(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (EntityBar bossBar : bossBars.values()) {
                bossBar.refresh();
            }
        }, 20L, 5L);
    }

    /**
     * Re-sends all active bars to a player after they change worlds, because entities are
     * scoped to the client's current world.
     *
     * @param e the world change event
     */
    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        for (EntityBar bossBar : bossBars.values()) {
            bossBar.refresh(e.getPlayer());
        }
    }

    /**
     * Removes the player's entity references when they leave the server.
     *
     * @param e the quit event
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        for (EntityBar bossBar : bossBars.values())
            bossBar.hide(e.getPlayer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideAll() {
        for (EntityBar bossBar : bossBars.values()) {
            bossBar.hideAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAll() {
        hideAll();
        bossBars.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showFor(String id, Player player) {
        EntityBar bossBar = ensureExists(id);
        bossBar.show(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showFor(String id, List<Player> players) {
        for (Player player : players)
            showFor(id, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideFor(String id, Player player) {
        EntityBar bossBar = ensureExists(id);
        bossBar.hide(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideFor(String id, List<Player> players) {
        for (Player player : players)
            hideFor(id, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateProgress(String id, float progress) {
        EntityBar bossBar = ensureExists(id);
        bossBar.setProgress(progress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTitle(String id, String title) {
        EntityBar bossBar = ensureExists(id);
        bossBar.setTitle(title);
    }

    /**
     * Returns the bar for the given identifier, creating a default empty bar with full
     * progress if it does not exist yet.
     *
     * @param id the bar identifier
     * @return the existing or newly created bar
     */
    private EntityBar ensureExists(String id) {
        if (!bossBars.containsKey(id)) {
            EntityBar bossBar = new EntityBar("§f ", 1.0f);
            bossBars.put(id, bossBar);
            return bossBar;
        }
        return bossBars.get(id);
    }
}