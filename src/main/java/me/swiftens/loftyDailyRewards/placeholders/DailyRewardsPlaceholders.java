package me.swiftens.loftyDailyRewards.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.ConfigManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DailyRewardsPlaceholders extends PlaceholderExpansion {

    private final DataManager dataManager;
    private final ConfigManager config;

    public DailyRewardsPlaceholders(DataManager dataManager, ConfigManager configManager) {
        this.dataManager = dataManager;
        this.config = configManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dailyrewards";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Swiftens";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        UUID playerId = player.getUniqueId();

        return switch (params.toLowerCase()) {
            case "current_streak" -> String.valueOf(dataManager.getCurrentStreak(playerId));
            case "highest_streak" -> String.valueOf(dataManager.getHighestStreak(playerId));
            case "time_remaining" -> config.getWaitingTime(dataManager.getTimeRemaining(playerId));
            default -> null;
        };

    }

}
