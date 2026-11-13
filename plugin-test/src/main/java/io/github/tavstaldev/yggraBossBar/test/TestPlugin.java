package io.github.tavstaldev.yggraBossBar.test;

import io.github.tavstaldev.yggraBossBar.bootstrap.YggraBossBar;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example plugin that shows how to bundle yggraBossBar, load Adventure at runtime with
 * Libby, and display a simple boss bar to joining players.
 */
@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin implements Listener {

    /** Libby manager used to download and load the Adventure libraries at runtime. */
    protected final BukkitLibraryManager libraryManager;

    /** Boss bar facade, created once the server is enabled. */
    private YggraBossBar _bar;

    /**
     * Creates the Libby manager and loads every Adventure artifact required by the library.
     * Libby does not resolve transitive dependencies, so each artifact is loaded explicitly.
     */
    public TestPlugin() {
        libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();
        libraryManager.addSonatype();
        libraryManager.addRepository("https://jitpack.io");

        //#region Adventure-API

        // net.kyori:examination-api
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .build());

        // net.kyori:examination-string
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .build());

        // net.kyori:adventure-key
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-api - Required by the Adventure-based implementations
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-legacy - Required by AdventureUtils
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-minimessage - Required by AdventureUtils
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-plain - Required by AdventureUtils
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-plain")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-gson - Required by AdventureUtils
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson")
                .version("4.26.1")
                .build());

        //#endregion
    }

    /**
     * Creates the boss bar facade and registers the join listener.
     */
    @Override
    public void onEnable() {
        _bar = new YggraBossBar(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has been loaded.");
    }

    /**
     * Logs that the example plugin has been disabled.
     */
    @Override
    public void onDisable() {
        getLogger().info("The plugin has been unloaded.");
    }

    /**
     * Displays an example boss bar to a player when they join.
     *
     * @param event the join event containing the player to show the bar to
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        _bar.updateTitle("test", "<green>Welcome &3&lto&r §6play.example.com");
        _bar.updateProgress("test", 0.5f);
        _bar.showFor("test", event.getPlayer());
    }
}
