package io.github.tavstaldev.yggraBossBar.bootstrap;

import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.bootstrap.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default {@link IBossBar} facade that delegates to the implementation matching the
 * running server version.
 * <p>
 * The implementation is selected once, when this facade is constructed, by inspecting
 * the Bukkit version:
 * <ul>
 *     <li>1.16.5 and newer &rarr; {@code impl.v1_16_5}</li>
 *     <li>1.13 to 1.16.4 &rarr; {@code impl.v1_13}</li>
 *     <li>1.9.4 to 1.12.2 &rarr; {@code impl.v1_9_4}</li>
 *     <li>1.8 to 1.9.3 &rarr; {@code impl.v1_8_8}</li>
 * </ul>
 * If the server version is unsupported, or the implementation cannot be loaded, a warning is
 * logged and every method of this facade becomes a no-op instead of throwing. This keeps a
 * plugin working on servers the library does not support yet.
 */
public class YggraBossBar implements IBossBar {

    /** The selected implementation, or {@code null} if none could be loaded. */
    private IBossBar _bar;

    /**
     * Creates the facade and resolves the implementation for the current server.
     * <p>
     * This constructor must be called after the server has fully started (for example in
     * {@code onEnable}) and after Adventure has been loaded. The supplied plugin is used to
     * register listeners and scheduled tasks for implementations that require them, and for
     * logging.
     *
     * @param plugin the owning plugin
     */
    public YggraBossBar(JavaPlugin plugin)  {
        try {
            String implClassName = getImplClassName();
            boolean usesPluginCtor =
                    VersionUtils.isAtLeast(1, 8) && !VersionUtils.isAtLeast(1, 9, 4)
                    ||
                    VersionUtils.isAtLeast(1, 13) && !VersionUtils.isAtLeast(1, 16, 5);


            if (usesPluginCtor)
                _bar = (IBossBar) Class.forName(implClassName)
                        .getDeclaredConstructor(JavaPlugin.class).newInstance(plugin);
            else
                _bar = (IBossBar) Class.forName(implClassName)
                        .getDeclaredConstructor().newInstance();
        }
        catch (Exception | LinkageError ex) {
            plugin.getLogger().warning("Failed to get the right IBossBar implementation for this server version.\n" + ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hideAll() {
        if (_bar == null)
            return;
        _bar.hideAll();
    }

    /** {@inheritDoc} */
    @Override
    public void clearAll() {
        if (_bar == null)
            return;
        _bar.clearAll();
    }

    /** {@inheritDoc} */
    @Override
    public void showFor(String id, Player player) {
        if (_bar == null)
            return;
        _bar.showFor(id, player);
    }

    /** {@inheritDoc} */
    @Override
    public void showFor(String id, List<Player> players) {
        if (_bar == null)
            return;
        _bar.showFor(id, players);
    }

    /** {@inheritDoc} */
    @Override
    public void hideFor(String id, Player player) {
        if (_bar == null)
            return;
        _bar.hideFor(id, player);
    }

    /** {@inheritDoc} */
    @Override
    public void hideFor(String id, List<Player> players) {
        if (_bar == null)
            return;
        _bar.hideFor(id, players);
    }

    /** {@inheritDoc} */
    @Override
    public void updateProgress(String id, float progress) {
        if (_bar == null)
            return;
        _bar.updateProgress(id, progress);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTitle(String id, String title) {
        if (_bar == null)
            return;
        _bar.updateTitle(id, title);
    }

    /**
     * Resolves the fully qualified class name of the implementation that matches the
     * running server version.
     *
     * @return the implementation class name
     * @throws RuntimeException if the server version is older than 1.8
     */
    private static @NotNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 16, 5))
            implClassName = "io.github.tavstaldev.yggraBossBar.impl.v1_16_5.BossBar";
        else if (VersionUtils.isAtLeast(1, 13))
            implClassName = "io.github.tavstaldev.yggraBossBar.impl.v1_13.BossBar";
        else if (VersionUtils.isAtLeast(1, 9, 4))
            implClassName = "io.github.tavstaldev.yggraBossBar.impl.v1_9_4.BossBar";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggraBossBar.impl.v1_8_8.BossBar";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
