package me.swiftens.loftyDailyRewards;

import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.placeholders.DailyRewardsPlaceholders;
import org.bukkit.Bukkit;
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
            new DailyRewardsPlaceholders(Bootstrapper.getInstance().getMessageManager(),
                    Bootstrapper.getInstance().getDataManager()).register();
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
        getCommand("dailyrewards").setExecutor(new DailyRewardsExecutor(this, Bootstrapper.getInstance().getMessageManager(),
                Bootstrapper.getInstance().getGuiManager(),
                Bootstrapper.getInstance().getDataManager()));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DailyRewardsListener(this,
                Bootstrapper.getInstance().getMessageManager(),
                Bootstrapper.getInstance().getGuiManager(),
                Bootstrapper.getInstance().getDataManager()), this);
    }

    private void registerReminder() {
        int interval = getConfig().getInt("reminder-interval");
        if (interval > 0) {
            interval = interval * 60 * 20;
            reminder = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player: Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("dailyrewards.open") && Bootstrapper.getInstance().getDataManager().canClaim(player.getUniqueId())) {
                            Bootstrapper.getInstance().getMessageManager()
                                    .sendMessage(player, MessageKeys.REMINDER_CAN_CLAIM, null);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(this, interval, interval);
        }
    }
}
