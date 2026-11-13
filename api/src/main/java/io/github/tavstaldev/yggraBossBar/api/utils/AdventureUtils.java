package io.github.tavstaldev.yggraBossBar.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for working with Adventure {@link Component} text.
 * <p>
 * Provides conversion from legacy Minecraft color codes ({@code &} and {@code §}) to
 * MiniMessage format, and translation of color-coded strings into Adventure
 * {@link Component} objects. Serializers for plain text, MiniMessage, legacy section
 * codes and JSON are exposed for reuse by the version-specific implementations.
 * <p>
 * This class is intended for internal use by the library and is not part of the public
 * API surface.
 */
@SuppressWarnings("unused")
@ApiStatus.Internal
public final class AdventureUtils {

    /** Shared MiniMessage instance used for parsing and serializing MiniMessage text. */
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /** Serializer for legacy section-sign ({@code §}) encoded text. */
    public static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();

    /** Serializer that strips all formatting and returns plain text. */
    public static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    /** Serializer for the GSON (JSON) component representation. */
    public static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    /** Matches a single legacy color or formatting code such as {@code &6} or {@code §l}. */
    private static final Pattern LEGACY_TO_MINI_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);

    /** Maps legacy color/formatting code characters to their MiniMessage tag equivalents. */
    private static final Map<Character, String> LEGACY_TO_MINI = new HashMap<>();

    static {
        LEGACY_TO_MINI.put('0', "<black>");
        LEGACY_TO_MINI.put('1', "<dark_blue>");
        LEGACY_TO_MINI.put('2', "<dark_green>");
        LEGACY_TO_MINI.put('3', "<dark_aqua>");
        LEGACY_TO_MINI.put('4', "<dark_red>");
        LEGACY_TO_MINI.put('5', "<dark_purple>");
        LEGACY_TO_MINI.put('6', "<gold>");
        LEGACY_TO_MINI.put('7', "<gray>");
        LEGACY_TO_MINI.put('8', "<dark_gray>");
        LEGACY_TO_MINI.put('9', "<blue>");
        LEGACY_TO_MINI.put('a', "<green>");
        LEGACY_TO_MINI.put('b', "<aqua>");
        LEGACY_TO_MINI.put('c', "<red>");
        LEGACY_TO_MINI.put('d', "<light_purple>");
        LEGACY_TO_MINI.put('e', "<yellow>");
        LEGACY_TO_MINI.put('f', "<white>");
        LEGACY_TO_MINI.put('l', "<bold>");
        LEGACY_TO_MINI.put('o', "<italic>");
        LEGACY_TO_MINI.put('n', "<underlined>");
        LEGACY_TO_MINI.put('m', "<strikethrough>");
        LEGACY_TO_MINI.put('k', "<obfuscated>");
        LEGACY_TO_MINI.put('r', "<reset>");
    }


    /**
     * Converts a legacy Minecraft color-coded string (using &sect; or &amp; prefixes)
     * into MiniMessage format.
     *
     * @param input the legacy string to convert
     * @return the converted MiniMessage string
     */
    private static @NotNull String legacyToMiniMessage(@NotNull String input) {
        Matcher matcher = LEGACY_TO_MINI_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            char code = Character.toLowerCase(matcher.group(1).charAt(0));
            String replacement = LEGACY_TO_MINI.getOrDefault(code, "");
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Parses a string into an Adventure {@link Component}, optionally checking for legacy color codes.
     * <p>
     * If {@code checkLegacy} is {@code false}, the input is treated as MiniMessage directly.
     * When enabled, the method detects {@code §} and {@code &} legacy color prefixes and
     * delegates to the appropriate serializer. If both are present, {@code §} is normalized
     * to {@code &} before deserialization.
     *
     * @param text        the input string (MiniMessage or legacy)
     * @param checkLegacy whether to probe for legacy section/ampersand codes
     * @return the parsed {@link Component} with italic decoration disabled
     */
    public static Component parse(@NotNull String text, boolean checkLegacy) {
        if (!checkLegacy) {
            return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        boolean hasAmpersand = text.indexOf('&') != -1;
        boolean hasSection = text.indexOf('§') != -1;
        if (!hasAmpersand && !hasSection) {
            return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
        }

        if (hasSection) {
            text = text.replace('§', '&');
        }

        String miniMessageText = legacyToMiniMessage(text);
        return MINI_MESSAGE
                .deserialize(miniMessageText)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Parses a string into an Adventure {@link Component}, automatically detecting legacy codes.
     * <p>
     * Convenience overload of {@link #parse(String, boolean)} with {@code checkLegacy = true}.
     *
     * @param text the input string (MiniMessage or legacy)
     * @return the parsed {@link Component} with italic decoration disabled
     */
    public static Component parse(@NotNull String text) {
        return parse(text, true);
    }

    /**
     * Serializes a {@link Component} to plain text, stripping all formatting.
     *
     * @param component the component to serialize
     * @return plain-text representation
     */
    public static @NotNull String toPlainText(@NotNull Component component) {
        return PLAIN_TEXT.serialize(component);
    }

    /**
     * Serializes a {@link Component} to the MiniMessage string format.
     *
     * @param component the component to serialize
     * @return MiniMessage string
     */
    public static @NotNull String toMiniMessage(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Serializes a {@link Component} to a legacy {@code §}-prefixed string.
     *
     * @param component the component to serialize
     * @return legacy section-sign-encoded string
     */
    public static @NotNull String toLegacy(@NotNull Component component) {
        return SECTION_SERIALIZER.serialize(component);
    }

    /**
     * Serializes a {@link Component} to its JSON (GSON) representation.
     *
     * @param component the component to serialize
     * @return JSON string
     */
    public static @NotNull String toJson(@NotNull Component component) {
        return GSON.serialize(component);
    }

    /**
     * Deserializes a JSON string back into an Adventure {@link Component}.
     *
     * @param text JSON representation of a component
     * @return the deserialized {@link Component}
     */
    public static @NotNull Component fromJson(@NotNull String text) {
        return GSON.deserialize(text);
    }
}
