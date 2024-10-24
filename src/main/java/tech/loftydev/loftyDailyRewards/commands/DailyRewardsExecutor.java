package tech.loftydev.loftyDailyRewards.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.loftydev.loftyDailyRewards.interfaces.DataManager;
import tech.loftydev.loftyDailyRewards.managers.GuiManager;
import tech.loftydev.loftyDailyRewards.statics.TextUtils;

import java.util.UUID;

public class DailyRewardsExecutor implements CommandExecutor {

    private final GuiManager guiManager;
    private final DataManager dataManager;

    public DailyRewardsExecutor(GuiManager guiManager, DataManager dataManager) {
        this.guiManager = guiManager;
        this.dataManager = dataManager;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // Open the Daily Rewards GUI
        UUID playerId;
        if (strings.length == 0) {
            if (commandSender instanceof Player player) {
                playerId = player.getUniqueId();
                int streak = dataManager.getCurrentStreak(playerId);
                guiManager.openGui(player, guiManager.getPageFromStreak(streak), streak + 1, dataManager.canClaim(playerId),
                        TextUtils.getTimeRemaining(dataManager.getLastClaim(playerId) + 86400000 - System.currentTimeMillis()), streak);
            }
        }
        return false;
    }
}
