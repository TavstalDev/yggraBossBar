package io.github.tavstaldev.yggraBossBar.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Version-independent entry point for creating and managing boss bars.
 * <p>
 * A boss bar is identified by a caller-chosen {@code id}. The same {@code id} can be reused across
 * multiple players, which makes it possible to drive one bar for a group of players at once. Bars
 * are created lazily the first time an {@code id} is used and are kept until
 * {@link #clearAll()} is called.
 * <p>
 * Implementations are resolved at runtime by the bootstrap module. If no implementation is
 * available, all methods are safe no-ops.
 */
@SuppressWarnings("unused")
public interface IBossBar {

    /**
     * Hides every managed boss bar from all of its current viewers.
     * <p>
     * The bars themselves are retained, so they can be shown again later with the same
     * {@code id}. Use {@link #clearAll()} to also discard the bars.
     */
    void hideAll();

    /**
     * Hides every managed boss bar from all of its current viewers and discards all bars.
     * <p>
     * After this call, any {@code id} that was used before will be recreated with default
     * values (empty title, full progress) the next time it is referenced.
     */
    void clearAll();

    /**
     * Shows the boss bar with the given {@code id} to a single player.
     * <p>
     * If the bar does not exist yet, a default bar is created. Adding a player that is already
     * a viewer has no additional effect.
     *
     * @param id     the identifier of the boss bar
     * @param player the player that should see the bar
     */
    void showFor(String id, Player player);

    /**
     * Shows the boss bar with the given {@code id} to every player in the list.
     *
     * @param id      the identifier of the boss bar
     * @param players the players that should see the bar
     * @see #showFor(String, Player)
     */
    void showFor(String id, List<Player> players);

    /**
     * Hides the boss bar with the given {@code id} from a single player.
     * <p>
     * If the bar does not exist yet, a default bar is created before it is hidden. Hiding a
     * player that is not a viewer has no additional effect.
     *
     * @param id     the identifier of the boss bar
     * @param player the player that should no longer see the bar
     */
    void hideFor(String id, Player player);

    /**
     * Hides the boss bar with the given {@code id} from every player in the list.
     *
     * @param id      the identifier of the boss bar
     * @param players the players that should no longer see the bar
     * @see #hideFor(String, Player)
     */
    void hideFor(String id, List<Player> players);

    /**
     * Updates the progress of the boss bar with the given {@code id}.
     * <p>
     * If the bar does not exist yet, a default bar is created and then updated. The new progress
     * is immediately visible to every current viewer.
     *
     * @param id       the identifier of the boss bar
     * @param progress the progress between {@code 0.0} (empty) and {@code 1.0} (full)
     */
    void updateProgress(String id, float progress);

    /**
     * Updates the title of the boss bar with the given {@code id}.
     * <p>
     * If the bar does not exist yet, a default bar is created and then updated. Titles may use
     * legacy Minecraft color codes ({@code &} or {@code §}) as well as MiniMessage formatting,
     * depending on the implementation selected for the running server.
     *
     * @param id    the identifier of the boss bar
     * @param title the new title, using legacy color codes or MiniMessage
     */
    void updateTitle(String id, String title);
}
