package me.swiftens.loftyDailyRewards.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
import me.swiftens.loftyDailyRewards.utils.TextUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DailyRewardsPlaceholders extends PlaceholderExpansion {

    private final DataManager dataManager;
    private final MessageManager messageManager;

    public DailyRewardsPlaceholders(MessageManager messageManager, DataManager dataManager) {
        this.messageManager = messageManager;
        this.dataManager = dataManager;
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

        switch (params.toLowerCase()) {
            case "current_streak": return String.valueOf(dataManager.getCurrentStreak(playerId));
            case "highest_streak": return String.valueOf(dataManager.getHighestStreak(playerId));
            case "time_remaining":
                if (dataManager.canClaim(playerId)) {
                    return messageManager.getCanClaimPlaceholder();
                } else {
                    return TextUtils.getTimeRemaining(dataManager.getTimeRemaining(playerId));
                }
        }

        return null;
    }

}
