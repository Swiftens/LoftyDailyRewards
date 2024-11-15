package me.swiftens.loftyDailyRewards.commands;

import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.managers.ConfigManager;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
import me.swiftens.loftyDailyRewards.managers.RewardsManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.GuiManager;

import java.util.UUID;

public class DailyRewardsExecutor implements CommandExecutor {

    private final ConfigManager config;
    private final GuiManager guiManager;
    private final DataManager dataManager;
    private final MessageManager messageManager;
    private final RewardsManager rewardsManager;

    public DailyRewardsExecutor(ConfigManager configManager, MessageManager messageManager, GuiManager guiManager, DataManager dataManager, RewardsManager rewardsManager) {
        this.config = configManager;
        this.messageManager = messageManager;
        this.guiManager = guiManager;
        this.dataManager = dataManager;
        this.rewardsManager = rewardsManager;
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
        int day;
        if (sender instanceof Player player) {
            playerId = player.getUniqueId();
            if (hasNoPermission(sender, "dailyrewards.open")) return;

            day = rewardsManager.getDay(dataManager.getCurrentStreak(playerId));

            guiManager.openGui(player, guiManager.getPageFromDay(day), day, dataManager.canClaim(playerId),
                    config.getWaitingTime(dataManager.getTimeRemaining(playerId)));
        } else {
            messageManager.simpleMessage(sender, MessageKeys.COMMAND_PLAYER_ONLY);
        }
    }

    private void handleSingleArg(CommandSender sender, String argument) {
        if (argument.equalsIgnoreCase("reload")) {
            if (hasNoPermission(sender, "dailyrewards.reload")) return;
            config.reload();
            rewardsManager.reload();
            messageManager.reload();
            messageManager.simpleMessage(sender, MessageKeys.COMMAND_CONFIG_RELOADED);
        } else if (argument.equalsIgnoreCase("open")) {
            handleNoArgs(sender);
        } else if (argument.equalsIgnoreCase("migrate")) {
            if (hasNoPermission(sender, "dailyrewards.migrate")) return;
            dataManager.migrate(null);
            messageManager.simpleMessage(sender, MessageKeys.COMMAND_MIGRATE_SUCCESSFUL);
        } else {
            manageHelpMessage(sender);
        }
    }

    private void handleMultiArgs(CommandSender sender, String[] args) {
        Player player;
        UUID playerId;
        switch (args[0].toLowerCase()) {
            case "skip" -> {
                if (hasNoPermission(sender, "dailyrewards.skip")) return;
                player = getPlayer(sender, args[1]);
                if (player == null) return;
                playerId = player.getUniqueId();
                dataManager.setLastClaim(playerId, System.currentTimeMillis() - 86400000);
                messageManager.simpleMessage(sender, MessageKeys.COMMAND_SKIP_SUCCESSFUL);
                messageManager.remindCanClaim(player);
            }
            case "set" -> {
                if (hasNoPermission(sender, "dailyrewards.set")) return;
                player = getPlayer(sender, args[2]);
                if (player == null) return;
                playerId = player.getUniqueId();

                try {
                    int amount = Integer.parseInt(args[3]);

                    if (amount < 0) {
                        messageManager.simpleMessage(sender, MessageKeys.COMMAND_INVALID_AMOUNT);
                        return;
                    }

                    if (args[1].equalsIgnoreCase("streak")) {
                        dataManager.setCurrentStreak(playerId, amount);
                        messageManager.messageChange(sender, player, amount, false);
                    } else if (args[1].equalsIgnoreCase("higheststreak")) {
                        dataManager.setHighestStreak(playerId, amount);
                        messageManager.messageChange(sender, player, amount, true);
                    } else {
                        manageHelpMessage(sender);
                    }
                } catch (NumberFormatException e) {
                    messageManager.simpleMessage(sender, MessageKeys.COMMAND_INVALID_AMOUNT);
                }

            }
            case "migrate" -> {
                if (hasNoPermission(sender, "dailyrewards.migrate")) return;
                player = getPlayer(sender, args[1]);
                if (player == null) return;
                playerId = player.getUniqueId();
                dataManager.migrate(playerId);
                messageManager.simpleMessage(sender, MessageKeys.COMMAND_MIGRATE_SUCCESSFUL);
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
            messageManager.simpleMessage(sender, MessageKeys.COMMAND_NO_PERMISSION);
            return true;
        }
        return false;
    }

    private Player getPlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            messageManager.simpleMessage(sender, MessageKeys.COMMAND_INVALID_PLAYER);
            return null;
        }
        return player;
    }

    private static final String[] BASE_HELP_MESSAGES = {
            "&e---- &bLoftyDailyRewards &e----",
            "&b[] - Optional&f, &a<> - Required",
            "&e/dailyrewards help",
            "&r &r Shows this list"
    };

    private void manageHelpMessage(CommandSender sender) {
        for (String message : BASE_HELP_MESSAGES) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        String reset = ChatColor.RESET + " " + ChatColor.RESET + " ";

        if (sender.hasPermission("dailyrewards.open")) {
            sender.sendMessage(ChatColor.YELLOW + "/dailyrewards " + ChatColor.AQUA + "[open]");
            sender.sendMessage(reset + "Open the DailyRewards GUI");
        }
        if (sender.hasPermission("dailyrewards.reload")) {
            sender.sendMessage(ChatColor.YELLOW + "/dailyrewards reload");
            sender.sendMessage(reset + "Reload the configuration files");
        }
        if (sender.hasPermission("dailyrewards.skip")) {
            sender.sendMessage(ChatColor.YELLOW + "/dailyrewards skip " + ChatColor.GREEN + "<player>");
            sender.sendMessage(reset + "Allow the player to claim as if it's the next day");
        }
        if (sender.hasPermission("dailyrewards.set")) {
            sender.sendMessage(ChatColor.YELLOW + "/dailyrewards set streak " + ChatColor.GREEN + "<player> <amount>");
            sender.sendMessage(reset + "Change the player's current streak");
            sender.sendMessage(ChatColor.YELLOW + "/dailyrewards set higheststreak " + ChatColor.GREEN +  "<player> <amount>");
            sender.sendMessage(reset + "Change the player's highest streak");
        }

    }
}
