package me.swiftens.loftyDailyRewards.managers;

import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.builders.MessageBuilder;
import me.swiftens.loftyDailyRewards.configs.MessageConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class MessageManager {

    @Getter
    private final BukkitAudiences audiences;
    private final Path messagePath;

    private MessageConfig messages;
    private String prefix;

    public MessageManager(LoftyDailyRewards core) {
        this.audiences = BukkitAudiences.create(core);
        this.messagePath = new File(core.getDataFolder(), "messages.yml").toPath();
        reload();
    }

    public void remindClaim(Player player, String time) {
        // If time is null, then the player can claim.
        Audience audience = audiences.player(player);
        if (time == null) {
            sendMessage(audience, messages.getCanClaim(), player, 0);
        } else {
            sendMessage(audiences.player(player), messages.getLoginCantClaim(), player, 0, time);
        }
    }

    public void messageChange(CommandSender sender, Player player, int streak, boolean highest) {
        Audience audience = audiences.sender(sender);
        Audience playerAudience = audiences.player(player);
        if (highest) {
            sendMessage(audience, messages.getCommands().getHighestStreakSetSuccessful().getMessage(), player, streak);
            sendMessage(playerAudience, messages.getCommands().getHighestStreakSetSuccessful().getNotify(), player, streak);
        } else {
            sendMessage(audience, messages.getCommands().getStreakSetSuccessful().getMessage(), player, streak);
            sendMessage(playerAudience, messages.getCommands().getStreakSetSuccessful().getNotify(), player, streak);
        }
    }

    // Make sure that you are using a LinkedHashMap
    public void sendLeaderboard(CommandSender sender, Map<String, Integer> leaderboard) {
        Audience audience = audiences.sender(sender);
        audience.sendMessage(new MessageBuilder("", messages.getLeaderboard().getHeader()).build());
        String preFormatted = messages.getLeaderboard().getItem();
        int rank = 1;
        MessageBuilder builder;
        for (Map.Entry<String, Integer> entry: leaderboard.entrySet()) {
            builder = new MessageBuilder("", preFormatted);

            builder.replace("{rank}", String.valueOf(rank))
                    .replace("{player}", entry.getKey())
                    .streak(entry.getValue());

            audience.sendMessage(builder.build());
        }
    }

    public void messageClaim(Player player, int streak, boolean broadcast) {
        sendMessage(audiences.player(player), messages.getClaimMessage(), player, streak);

        if (broadcast) {
            sendMessage(audiences.all(), messages.getClaimBroadcast(), player, streak);
        }
    }

    public void simpleMessage(CommandSender sender, Extras extra) {
        String message = "";
        switch (extra) {
            case COMMAND_PLAYER_ONLY -> message = messages.getCommands().getPlayerOnly();
            case COMMAND_NO_PERMISSION -> message = messages.getCommands().getNoPermission();
            case COMMAND_INVALID_PLAYER -> message = messages.getCommands().getInvalidPlayer();
            case COMMAND_CONFIG_RELOADED -> message = messages.getCommands().getConfigReloaded();
            case COMMAND_SKIP_SUCCESSFUL -> message = messages.getCommands().getSkipSuccessful();
            case COMMAND_MIGRATE_SUCESSFUL -> message = messages.getCommands().getMigrateSuccessful();
            case COMMAND_INVALID_AMOUNT -> message = messages.getCommands().getInvalidAmount();
        }
        simpleMessage(sender, message);
    }

    public void simpleMessage(CommandSender sender, String message) {
        sendMessage(audiences.sender(sender), message, null, 0, null);
    }

    public void reload() {
        this.messages = YamlConfigurations.update(
                messagePath,
                MessageConfig.class,
                LoftyDailyRewards.PROPERTIES
        );

        prefix = messages.getPrefix();
    }

    private void sendMessage(Audience audience, String message, Player player, int streak) {
        sendMessage(audience, message, player, streak, null);
    }

    private void sendMessage(Audience audience, String message, Player player, int streak, String time) {
        if (StringUtils.isEmpty(message)) return;
        MessageBuilder builder = new MessageBuilder(prefix, message);

        if (player != null) builder = builder.player(player);
        if (streak > 0) builder = builder.streak(streak);
        if (time != null) builder = builder.time(time);

        audience.sendMessage(builder.build());
    }

    public enum Extras {
        COMMAND_NO_PERMISSION,
        COMMAND_PLAYER_ONLY,
        COMMAND_CONFIG_RELOADED,
        COMMAND_SKIP_SUCCESSFUL,
        COMMAND_INVALID_PLAYER,
        COMMAND_MIGRATE_SUCESSFUL,
        COMMAND_INVALID_AMOUNT
    }

}
