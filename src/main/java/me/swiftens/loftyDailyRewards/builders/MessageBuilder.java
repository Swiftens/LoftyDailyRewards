package me.swiftens.loftyDailyRewards.builders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.swiftens.loftyDailyRewards.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class MessageBuilder {

    private final String prefix;
    private String message;

    public MessageBuilder(String prefix, String message) {
        this.prefix = prefix;
        this.message = message;
    }


    public MessageBuilder replace(String placeholder, String value) {
        this.message = this.message.replace(placeholder, value);
        return this;
    }

    public MessageBuilder player(Player player) {
        this.message = PlaceholderAPI.setPlaceholders(player, message);
        this.message = message.replace("{player}", player.getName());
        this.message = message.replace("{display_name}", player.getDisplayName());
        return this;
    }

    public MessageBuilder time(String time) {
        this.message = message.replace("{time}", time);
        return this;
    }

    public MessageBuilder streak(int streak) {
        this.message = message.replace("{streak}", String.valueOf(streak));
        return this;
    }

    public Component build() {
        if (StringUtils.isEmpty(this.message)) return null;
        String finalMessage = this.message.trim();

        if (!prefix.isEmpty())
            finalMessage = String.format("%s%s", this.prefix, finalMessage);

        return TextUtils.compileMiniMessage(finalMessage);
    }

}
