package io.github.tavstaldev.yggraBossBar.impl.v1_8_8.models;

import io.github.tavstaldev.yggraBossBar.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Client-side boss bar entity used by the 1.8 implementation.
 * <p>
 * Minecraft 1.8 has no boss bar API, so an invisible wither is spawned for every viewer and
 * its health is used as the visible bar progress. All NMS access is reflective, which allows
 * the same binary to run on several 1.8.x server revisions.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class EntityBar {
    /** Raw title, which may contain legacy color codes. */
    private String title;

    /** Progress between {@code 0.0} and {@code 1.0}. */
    private float progress;

    /** NMS package version, for example {@code v1_8_R3}. */
    private static final String V =
            Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    /** Live wither entity per viewer, keyed by player UUID. */
    private final Map<UUID, Object> entities = new HashMap<>();

    /** Cached reflective handles to the NMS classes and members used by this class. */
    private static Method getPlayerHandle, getWorldHandle, sendPacket;
    private static Method setLocation, setInvisible, setCustomName, setHealth, getId, getDataWatcher, watcher;
    private static Constructor<?> mobCtor, spawnCtor, destroyCtor, metaCtor, teleportCtor;
    private static Field connectionField;

    /*
     * Resolves every reflective member once. A failure here is reported immediately and
     * leaves the handles null; subsequent show/hide calls will then fail as well.
     */
    static {
        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + V + ".entity.CraftPlayer");
            Class<?> craftWorld  = Class.forName("org.bukkit.craftbukkit." + V + ".CraftWorld");
            Class<?> entity      = nms("Entity");
            Class<?> entityLiving = nms("EntityLiving");
            Class<?> entityPlayer = nms("EntityPlayer");
            Class<?> world       = nms("World");
            Class<?> mob      = nms("EntityWither");
            Class<?> packet      = nms("Packet");
            Class<?> dataWatcher = nms("DataWatcher");


            getPlayerHandle = craftPlayer.getMethod("getHandle");
            getWorldHandle  = craftWorld.getMethod("getHandle");
            connectionField = entityPlayer.getField("playerConnection");
            sendPacket      = nms("PlayerConnection").getMethod("sendPacket", packet);

            mobCtor = mob.getConstructor(world);
            setLocation   = entity.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            setInvisible  = entity.getMethod("setInvisible", boolean.class);
            setCustomName = entity.getMethod("setCustomName", String.class);
            setHealth     = entityLiving.getMethod("setHealth", float.class);
            getId         = entity.getMethod("getId");
            getDataWatcher = entity.getMethod("getDataWatcher");
            watcher        = dataWatcher.getMethod("watch", int.class, Object.class);

            spawnCtor    = nms("PacketPlayOutSpawnEntityLiving").getConstructor(entityLiving);
            destroyCtor  = nms("PacketPlayOutEntityDestroy").getConstructor(int[].class);
            metaCtor     = nms("PacketPlayOutEntityMetadata").getConstructor(int.class, dataWatcher, boolean.class);
            teleportCtor = nms("PacketPlayOutEntityTeleport").getConstructor(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolves an NMS class for the detected server revision.
     *
     * @param name the simple NMS class name, for example {@code EntityWither}
     * @return the loaded class
     * @throws ClassNotFoundException if the class does not exist on this server version
     */
    private static Class<?> nms(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + V + "." + name);
    }

    /**
     * Creates a bar description. The client entity itself is spawned lazily by
     * {@link #show(Player)}.
     *
     * @param title    the initial title, which may contain legacy color codes
     * @param progress the initial progress between {@code 0.0} and {@code 1.0}
     */
    public EntityBar(String title, float progress) {
        this.title = title;
        this.progress = progress;
    }

    /**
     * Shows the bar to a player, spawning the wither on first use and updating it afterwards.
     * <p>
     * The entity is placed roughly 60 blocks in front of the player's eyes and given the
     * current title and health. Exceptions from the reflective calls are printed and swallowed
     * so a failure in one viewer does not affect the others.
     *
     * @param player the viewer
     */
    public void show(Player player) {
        try {
            float health = (float) Math.max(1.0, progress * 300.0);
            Location eye = player.getEyeLocation();
            Location loc = eye.clone().add(eye.getDirection().multiply(60));

            Object entity = entities.get(player.getUniqueId());
            if (entity == null) {
                entity = mobCtor.newInstance(getWorldHandle.invoke(player.getWorld()));
                setLocation.invoke(entity, loc.getX(), loc.getY(), loc.getZ(), 0f, 0f);
                setInvisible.invoke(entity, true);
                setCustomName.invoke(entity, title);
                setHealth.invoke(entity, health);
                Object dw = getDataWatcher.invoke(entity);
                watcher.invoke(dw, 20, 880);
                watcher.invoke(dw, 0, (byte) (1 << 5));
                entities.put(player.getUniqueId(), entity);
                send(player, spawnCtor.newInstance(entity));
            } else {
                setLocation.invoke(entity, loc.getX(), loc.getY(), loc.getZ(), 0f, 0f);
                setCustomName.invoke(entity, title);
                setHealth.invoke(entity, health);
                send(player, metaCtor.newInstance(getId.invoke(entity), getDataWatcher.invoke(entity), true));
                send(player, teleportCtor.newInstance(entity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides the bar from a single player by destroying their wither entity.
     *
     * @param player the viewer
     */
    public void hide(Player player) {
        Object wither = entities.remove(player.getUniqueId());
        if (wither == null)
            return;
        try {
            send(player, destroyCtor.newInstance((Object) new int[]{(int) getId.invoke(wither)}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides the bar from every viewer by destroying their wither entities and clearing
     * the tracked viewers.
     */
    public void hideAll() {
        for (UUID id : entities.keySet()) {
            Object wither = entities.remove(id);
            Player player = Bukkit.getPlayer(id);
            if (wither == null || player == null)
                return;
            try {
                send(player, destroyCtor.newInstance((Object) new int[]{(int) getId.invoke(wither)}));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the title and immediately refreshes all current viewers.
     *
     * @param title the new title, which may contain legacy color codes
     */
    public void setTitle(String title) {
        Component c = AdventureUtils.parse(title);
        this.title = AdventureUtils.toLegacy(c);
        refresh();
    }

    /**
     * Updates the progress and immediately refreshes all current viewers.
     *
     * @param progress the new progress between {@code 0.0} and {@code 1.0}
     */
    public void setProgress(float progress) {
        this.progress = progress;
        refresh();
    }

    /**
     * Re-sends the bar to one viewer, if that player is currently tracked.
     *
     * @param player the viewer to refresh
     */
    public void refresh(Player player) {
        if (!entities.containsKey(player.getUniqueId()))
            return;
        show(player);
    }

    /**
     * Re-sends the bar to every tracked viewer.
     */
    public void refresh() {
        for (UUID id : entities.keySet()) {
            Player player = Bukkit.getPlayer(id);
            show(player);
        }
    }

    /**
     * Sends one packet to a player's connection.
     *
     * @param p      the recipient
     * @param packet the NMS packet to send
     * @throws Exception if the reflective send fails
     */
    private void send(Player p, Object packet) throws Exception {
        sendPacket.invoke(connectionField.get(getPlayerHandle.invoke(p)), packet);
    }
}
