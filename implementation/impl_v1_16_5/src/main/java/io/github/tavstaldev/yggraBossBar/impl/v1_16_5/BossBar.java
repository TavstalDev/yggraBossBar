package io.github.tavstaldev.yggraBossBar.impl.v1_16_5;

import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.api.utils.AdventureUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Boss bar implementation for Minecraft 1.16.5 and newer.
 * <p>
 * Uses the Adventure boss bar API that is available directly on {@link Player} in this
 * version range. Bars are stored per identifier and shared between all of their viewers.
 */
public class BossBar implements IBossBar {
    /** Bars managed by this implementation, keyed by caller-chosen identifier. */
    private final HashMap<String, net.kyori.adventure.bossbar.BossBar> bossBars = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideAll() {
        for (net.kyori.adventure.bossbar.BossBar bossBar : bossBars.values()) {
            bossBar.viewers().forEach(x -> {
                if (x instanceof Audience)
                    bossBar.removeViewer((Audience)x);
            });
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
        net.kyori.adventure.bossbar.BossBar bossBar = ensureExists(id);
        player.showBossBar(bossBar);
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
        net.kyori.adventure.bossbar.BossBar bossBar = ensureExists(id);
        player.hideBossBar(bossBar);
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
        net.kyori.adventure.bossbar.BossBar bossBar = ensureExists(id);
        bossBar.progress(progress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTitle(String id, String title) {
        net.kyori.adventure.bossbar.BossBar bossBar = ensureExists(id);
        bossBar.name(AdventureUtils.parse(title, true));
    }

    /**
     * Returns the bar for the given identifier, creating a default blue bar with full
     * progress if it does not exist yet.
     *
     * @param id the bar identifier
     * @return the existing or newly created bar
     */
    private net.kyori.adventure.bossbar.BossBar ensureExists(String id) {
        if (!bossBars.containsKey(id)) {
            net.kyori.adventure.bossbar.BossBar bossBar = net.kyori.adventure.bossbar.BossBar.bossBar(
                    Component.text(""),
                    1.0f,
                    net.kyori.adventure.bossbar.BossBar.Color.BLUE,
                    net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS
            );
            bossBars.put(id, bossBar);
            return bossBar;
        }
        return bossBars.get(id);
    }
}
