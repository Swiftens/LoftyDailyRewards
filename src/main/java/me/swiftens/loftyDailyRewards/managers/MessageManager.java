package me.swiftens.loftyDailyRewards.managers;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.builders.MessageBuilder;
import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.utils.YamlAccess;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageManager {

    private final BukkitAudiences audiences;

    private final YamlAccess messages;
    private String prefix;

    public MessageManager(LoftyDailyRewards core, BukkitAudiences audiences) {
        this.audiences = audiences;
        this.messages = new YamlAccess(core, "messages.yml");
        messages.saveDefaultConfig();
        messages.getFile().options().copyDefaults(true);
        messages.saveFile();
        reload();
    }

    public void remindCanClaim(Player player) {
        sendMessage(audiences.player(player), MessageKeys.REMINDER_CAN_CLAIM, player, 0, null);
    }

    public void remindCantClaim(Player player, String time) {
        sendMessage(audiences.player(player), MessageKeys.REMINDER_CANT_CLAIM, player, 0, time);
    }

    public void notifyChange(Player player, int streak, boolean highest) {
        Audience audience = audiences.player(player);
        if (highest) {
            sendMessage(audience, MessageKeys.COMMAND_HIGHEST_STREAK_SUCCESSFUL_NOTIFY, player, streak, null);
        } else {
            sendMessage(audience, MessageKeys.COMMAND_STREAK_SUCCESSFUL_NOTIFY, player, streak, null);
        }
    }

    public void messageChange(CommandSender sender, Player player, int streak, boolean highest) {
        Audience audience = audiences.sender(sender);
        if (highest) {
            sendMessage(audience, MessageKeys.COMMAND_HIGHEST_STREAK_SUCCESSFUL, player, streak, null);
        } else {
            sendMessage(audience, MessageKeys.COMMAND_STREAK_SUCCESSFUL, player, streak, null);
        }
    }

    // Make sure that you are using a LinkedHashMap
    public void sendLeaderboard(CommandSender sender, Map<String, Integer> leaderboard) {
        Audience audience = audiences.sender(sender);
        String preFormatted = messages.getFile().getString(MessageKeys.LEADERBOARD_ITEM.getKey()), formatted;
        audience.sendMessage(new MessageBuilder("", messages.getFile().getString(MessageKeys.LEADERBOARD_HEADER.getKey())).build());
        int i = 1;
        for (Map.Entry<String, Integer> entry: leaderboard.entrySet()) {
            MessageBuilder builder = new MessageBuilder("", preFormatted);

            builder.replace("{rank}", String.valueOf(i));
            builder.replace("{player}", entry.getKey());
            builder.streak(entry.getValue());

            audience.sendMessage(builder.build());
        }

    }

    public void messageClaim(Player player, int streak, boolean broadcast) {
        sendMessage(
                audiences.player(player),
                MessageKeys.COMMAND_CLAIM_MESSAGE,
                player,
                streak,
                null
        );

        if (broadcast) {
            sendMessage(
                    audiences.all(),
                    MessageKeys.CLAIM_BROADCAST,
                    player,
                    streak,
                    null
            );
        }
    }


    public void simpleMessage(CommandSender sender, MessageKeys key) {
        sendMessage(audiences.sender(sender), key, null, 0, null);
    }

    public void reload() {
        messages.reloadFile();

        prefix = messages.getFile().getString("prefix");
    }

    private void sendMessage(Audience audience, MessageKeys key, Player player, int streak, String time) {
        String message = messages.getFile().getString(key.getKey());
        if (StringUtils.isEmpty(message)) return;
        MessageBuilder builder = new MessageBuilder(prefix, message);

        if (player != null) builder = builder.player(player);
        if (streak > 0) builder = builder.streak(streak);
        if (time != null) builder = builder.time(time);

        audience.sendMessage(builder.build());
    }


}
