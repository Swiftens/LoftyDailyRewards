package me.swiftens.loftyDailyRewards.commands;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.GuiManager;
import me.swiftens.loftyDailyRewards.utils.TextUtils;

import java.util.UUID;

public class DailyRewardsExecutor implements CommandExecutor {

    private final LoftyDailyRewards core;
    private final GuiManager guiManager;
    private final DataManager dataManager;
    private final MessageManager messageManager;

    public DailyRewardsExecutor(LoftyDailyRewards core, MessageManager messageManager, GuiManager guiManager, DataManager dataManager) {
        this.core = core;
        this.messageManager = messageManager;
        this.guiManager = guiManager;
        this.dataManager = dataManager;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // Open the Daily Rewards GUI

        if (strings.length == 0) {
            handleNoArgs(commandSender);
            return true;
        } else if (strings.length == 1) {
            handleSingleArg(commandSender, strings[0]);
            return true;
        } else {
            handleMultiArgs(commandSender, strings);
            return true;
        }
        
    }

    private void handleNoArgs(CommandSender sender) {
        UUID playerId;
        if (sender instanceof Player player) {
            playerId = player.getUniqueId();
            if (hasNoPermission(sender, "dailyrewards.reload")) return;
            int streak = dataManager.getCurrentStreak(playerId);
            guiManager.openGui(player, guiManager.getPageFromStreak(streak), streak + 1, dataManager.canClaim(playerId),
                    TextUtils.getTimeRemaining(dataManager.getTimeRemaining(playerId)), streak);
        } else {
            messageManager.sendMessage(sender, MessageKeys.COMMAND_PLAYER_ONLY, null);

        }
    }

    private void handleSingleArg(CommandSender sender, String argument) {
        if (argument.equalsIgnoreCase("reload")) {
            if (hasNoPermission(sender, "dailyrewards.reload")) return;
            core.reloadConfig();
            messageManager.reload();
        } else {
            manageHelpMessage(sender);
        }
    }

    private void handleMultiArgs(CommandSender sender, String[] args) {
        Player player;
        UUID playerId;
        switch (args[0].toLowerCase()) {
            case "skip" -> {
                player = getPlayer(sender, args[1]);
                if (player == null) return;
                playerId = player.getUniqueId();
                dataManager.setLastClaim(playerId, System.currentTimeMillis() - 86400000);
                messageManager.sendMessage(sender, MessageKeys.COMMAND_SKIP_SUCCESSFUL, null);
            }
            case "set" -> {
                player = getPlayer(sender, args[1]);
                if (player == null) return;
                playerId = player.getUniqueId();

                try {
                    int amount = Integer.parseInt(args[3]);
                    if (args[2].equalsIgnoreCase("streak")) {
                        dataManager.setCurrentStreak(playerId, amount);
                        messageManager.sendMessage(sender, MessageKeys.COMMAND_STREAK_SUCCESSFUL, null);
                    } else if (args[2].equalsIgnoreCase("higheststreak")) {
                        dataManager.setHighestStreak(playerId, amount);
                        messageManager.sendMessage(sender, MessageKeys.COMMAND_HIGHEST_STREAK_SUCCESSFUL, null);
                    } else {
                        manageHelpMessage(sender);
                    }
                } catch (NumberFormatException e) {
                    messageManager.sendMessage(sender, MessageKeys.COMMAND_INVALID_AMOUNT, null);
                }

            }
            default -> manageHelpMessage(sender);
        }
    }

    /*
    * Check if the sender has the right permission
    * Send no permission messages otherwise.
    *
    * @param sender the command sender
    * @param permission the permission to check
    *
    * @return true if the sender has no permission
     */
    private boolean hasNoPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            messageManager.sendMessage(sender, MessageKeys.COMMAND_NO_PERMISSION, null);
            return true;
        }
        return false;
    }

    private Player getPlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            messageManager.sendMessage(sender, MessageKeys.COMMAND_INVALID_PLAYER, null);
            return null;
        }
        return player;
    }

    private static final String[] BASE_HELP_MESSAGES = {
            "&e---- &bLoftyDailyRewards &e----",
            "&b[] - Optional&f, &e<> - Required",
            "&e/dailyrewards help",
            "&r &r Shows this list"
    };

    private void manageHelpMessage(CommandSender sender) {
        for (String message : BASE_HELP_MESSAGES) {
            sender.sendMessage(TextUtils.translateHexCodes(message));
        }

        if (sender.hasPermission("dailyrewards.open")) {
            sender.sendMessage(TextUtils.translateHexCodes("&e/dailyrewards &b[open]"));
            sender.sendMessage(TextUtils.translateHexCodes("&r &r Open the DailyRewards GUI"));
        }
        if (sender.hasPermission("dailyrewards.reload")) {
            sender.sendMessage(TextUtils.translateHexCodes("&e/dailyrewards reload"));
            sender.sendMessage(TextUtils.translateHexCodes("&r &r Reload the configuration files"));
        }
        if (sender.hasPermission("dailyrewards.skip")) {
            sender.sendMessage(TextUtils.translateHexCodes("&e/dailyrewards skip"));
            sender.sendMessage(TextUtils.translateHexCodes("&r &r Allow the player to claim as if it's the next day"));
        }
        if (sender.hasPermission("dailyrewards.set")) {
            sender.sendMessage(TextUtils.translateHexCodes("&e/dailyrewards set <player> streak <amount>"));
            sender.sendMessage(TextUtils.translateHexCodes("&r &r Change the player's current streak"));
            sender.sendMessage(TextUtils.translateHexCodes("&e/dailyrewards set <player> higheststreak <amount>"));
            sender.sendMessage(TextUtils.translateHexCodes("&r &r Change the player's highest streak"));
        }

    }
}
