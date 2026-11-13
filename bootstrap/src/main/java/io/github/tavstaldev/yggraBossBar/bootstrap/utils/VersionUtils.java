package io.github.tavstaldev.yggraBossBar.bootstrap.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Method;

/**
 * Utility class for handling server version-related operations.
 *
 * <p>The detected server version is cached after the first lookup, so repeated calls
 * to {@link #getServerVersion()} are cheap and consistent.</p>
 *
 * <p>This class is intended for internal use by the library and is not part of the public
 * API surface.</p>
 */
@ApiStatus.Internal
public class VersionUtils {
    /** Cached server version, or {@code null} until the first lookup. */
    private static String _version = null;

    /**
     * Retrieves the current server version as a dotted numeric string.
     *
     * <p>The value is read from {@code Bukkit#getMinecraftVersion()} when available, with a
     * fallback to the CraftBukkit package name for older servers. The result is cached.</p>
     *
     * @return the server version, for example {@code 1.20.4} or {@code 1.8.3}
     */
    public static String getServerVersion() {
        if (_version != null) {
            return _version;
        }

        String version;
        try {
            Method method = Bukkit.class.getDeclaredMethod("getMinecraftVersion");
            version = (String) method.invoke(null);
        } catch (Exception e) {
            // Fallback for older versions
            version = Bukkit.getServer().getClass().getPackage().getName()
                    .split("\\.")[3]
                    .replace("_", ".")
                    .replaceAll("[A-Z]|[a-z]", "");
        }
        _version = version;
        return version;
    }

    /**
     * Checks if the server version is at least the specified major and minor version.
     * <p>
     * Any patch component of the server version is ignored.
     *
     * @param major The major version to compare against.
     * @param minor The minor version to compare against.
     * @return True if the server version is at least the specified version, false otherwise.
     * @throws NumberFormatException if the detected server version cannot be parsed
     */
    public static boolean isAtLeast(int major, int minor) {
        String[] parts = getServerVersion().split("\\.");
        int maj = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return (maj > major) || (maj == major && min >= minor);
    }

    /**
     * Checks if the server version is at least the specified major, minor, and patch version.
     * This method parses the server version string and compares it against the provided version numbers.
     * If the patch version is not specified in the server version, it defaults to 0.
     *
     * @param major The major version to compare against.
     * @param minor The minor version to compare against.
     * @param patch The patch version to compare against.
     * @return True if the server version is at least the specified version, false otherwise.
     * @throws NumberFormatException if the detected server version cannot be parsed
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        String[] parts = getServerVersion().split("\\.");
        int maj = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        int pat;
        if (parts.length > 2) {
            pat = Integer.parseInt(parts[2]);
        } else {
            pat = 0; // Default to 0 if patch version is not specified
        }
        return maj > major || maj == major && min > minor || maj == major && min == minor && pat >= patch;
    }
}