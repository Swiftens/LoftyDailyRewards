package me.swiftens.loftyDailyRewards;

import me.swiftens.loftyDailyRewards.commands.DailyRewardsTabCompleter;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
import me.swiftens.loftyDailyRewards.placeholders.DailyRewardsPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.swiftens.loftyDailyRewards.commands.DailyRewardsExecutor;
import me.swiftens.loftyDailyRewards.listeners.DailyRewardsListener;
import me.swiftens.loftyDailyRewards.statics.Bootstrapper;

import java.sql.SQLException;

public final class LoftyDailyRewards extends JavaPlugin {

    BukkitTask reminder;

    @Override
    public void onEnable() {
        Bootstrapper.getInstance().initialize(this);

        registerCommands();
        registerListeners();
        registerReminder();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DailyRewardsPlaceholders(Bootstrapper.getInstance().getDataManager(),
                    Bootstrapper.getInstance().getConfigManager()).register();
        }
    }

    @Override
    public void onDisable() {

        if (reminder != null) {
            reminder.cancel();
            reminder = null;
        }

        try {
            Bootstrapper.getInstance().getDataManager().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommands() {
        Bootstrapper instance = Bootstrapper.getInstance();
        PluginCommand command = getCommand("dailyrewards");
        command.setExecutor(new DailyRewardsExecutor(instance.getConfigManager(),
                instance.getMessageManager(),
                instance.getGuiManager(),
                instance.getDataManager(),
                instance.getRewardsManager()));

        command.setTabCompleter(new DailyRewardsTabCompleter());
    }

    private void registerListeners() {
        Bootstrapper instance = Bootstrapper.getInstance();
        Bukkit.getPluginManager().registerEvents(new DailyRewardsListener(instance.getConfigManager(),
                instance.getMessageManager(),
                instance.getGuiManager(),
                instance.getDataManager(),
                instance.getRewardsManager()), this);
    }

    private void registerReminder() {
        int interval = getConfig().getInt("reminder-interval");
        if (interval > 0) {
            interval = interval * 60 * 20;
            reminder = new BukkitRunnable() {
                @Override
                public void run() {
                    DataManager dataManager = Bootstrapper.getInstance().getDataManager();
                    MessageManager messageManager = Bootstrapper.getInstance().getMessageManager();
                    for (Player player: Bukkit.getOnlinePlayers()) {
                        if (dataManager.canClaim(player.getUniqueId()) && player.hasPermission("dailyrewards.open")) {
                            messageManager.remindCanClaim(player);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(this, interval, interval);
        }
    }
}
