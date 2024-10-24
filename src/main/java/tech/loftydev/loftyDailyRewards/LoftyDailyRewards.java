package tech.loftydev.loftyDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tech.loftydev.loftyDailyRewards.commands.DailyRewardsExecutor;
import tech.loftydev.loftyDailyRewards.listeners.DailyRewardsListener;
import tech.loftydev.loftyDailyRewards.statics.Bootstrapper;

import java.sql.SQLException;

public final class LoftyDailyRewards extends JavaPlugin {

    @Override
    public void onEnable() {
        Bootstrapper.getInstance().initialize(this);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Close database!
        try {
            Bootstrapper.getInstance().getDataManager().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommands() {
        getCommand("dailyrewards").setExecutor(new DailyRewardsExecutor(Bootstrapper.getInstance().getGuiManager(), Bootstrapper.getInstance().getDataManager()));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DailyRewardsListener(this, Bootstrapper.getInstance().getGuiManager(), Bootstrapper.getInstance().getDataManager()), this);
    }
}
