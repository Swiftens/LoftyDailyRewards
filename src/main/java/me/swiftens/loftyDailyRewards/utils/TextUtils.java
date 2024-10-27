package me.swiftens.loftyDailyRewards.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    public static String translateHexCodes(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder buffer = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String translateLegacyToMiniMessage(String text) {

        return text.replaceAll("&0", "<black>")
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&m", "<strikethrough>")
                .replaceAll("&k", "<obfuscated>")
                .replaceAll("&o", "<italic>")
                .replaceAll("&l", "<bold>")
                .replaceAll("&r", "<reset>")
                .replaceAll(HEX_PATTERN.pattern(), "<#$1>");
    }

    public static Component compileMiniMessage(String text) {
        text = translateLegacyToMiniMessage(text);

        Component component = MiniMessage.miniMessage().deserialize(text);

        if (component.decorations().get(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
            component = component.decoration(TextDecoration.ITALIC, false);
        }

        return component;
    }



}
