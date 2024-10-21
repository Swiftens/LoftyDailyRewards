package tech.loftydev.loftyDailyRewards;

import org.bukkit.plugin.java.JavaPlugin;
import tech.loftydev.loftyDailyRewards.statics.Bootstrapper;

import java.sql.SQLException;

public final class LoftyDailyRewards extends JavaPlugin {

    @Override
    public void onEnable() {
        Bootstrapper.getInstance().initialize(this);

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
}
