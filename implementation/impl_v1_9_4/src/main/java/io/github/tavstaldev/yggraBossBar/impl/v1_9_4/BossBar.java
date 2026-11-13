package io.github.tavstaldev.yggraBossBar.impl.v1_9_4;

import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Boss bar implementation for Minecraft 1.9.4 through 1.12.2.
 * <p>
 * Uses the Bukkit boss bar API. Titles are passed through as-is, so legacy section-sign
 * ({@code §}) color codes are supported but MiniMessage is not.
 */
public class BossBar implements IBossBar {
    /** Bars managed by this implementation, keyed by caller-chosen identifier. */
    private final HashMap<String, org.bukkit.boss.BossBar> bossBars = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideAll() {
        for (org.bukkit.boss.BossBar bossBar : bossBars.values()) {
            for (Player player : bossBar.getPlayers())
                bossBar.removePlayer(player);
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
        org.bukkit.boss.BossBar bossBar = ensureExists(id);
        bossBar.addPlayer(player);
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
        org.bukkit.boss.BossBar bossBar = ensureExists(id);
        bossBar.removePlayer(player);
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
        org.bukkit.boss.BossBar bossBar = ensureExists(id);
        bossBar.setProgress(progress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTitle(String id, String title) {
        org.bukkit.boss.BossBar bossBar = ensureExists(id);
        Component c = AdventureUtils.parse(title);
        String name = AdventureUtils.toLegacy(c);
        bossBar.setTitle(name);
    }

    /**
     * Returns the bar for the given identifier, creating a default purple solid bar with
     * full progress if it does not exist yet.
     *
     * @param id the bar identifier
     * @return the existing or newly created bar
     */
    private org.bukkit.boss.BossBar ensureExists(String id) {
        if (!bossBars.containsKey(id)) {
            org.bukkit.boss.BossBar bossBar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
            bossBars.put(id, bossBar);
            return bossBar;
        }
        return bossBars.get(id);
    }
}