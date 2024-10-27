package me.swiftens.loftyDailyRewards.listeners;

import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.managers.ConfigManager;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
import me.swiftens.loftyDailyRewards.managers.RewardsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.GuiManager;
import me.swiftens.loftyDailyRewards.utils.TextUtils;

import java.util.UUID;

public class DailyRewardsListener implements Listener {

    private final ConfigManager config;
    private final GuiManager guiManager;
    private final DataManager dataManager;
    private final MessageManager messageManager;
    private final RewardsManager rewardsManager;

    public DailyRewardsListener(ConfigManager config, MessageManager messageManager, GuiManager guiManager, DataManager dataManager, RewardsManager rewardsManager) {
        this.config = config;
        this.messageManager = messageManager;
        this.guiManager = guiManager;
        this.dataManager = dataManager;
        this.rewardsManager = rewardsManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        dataManager.setDefaultData(player.getUniqueId());

        if (player.hasPermission("dailyrewards.open")) {
            if (dataManager.canClaim(player.getUniqueId())) {
                messageManager.remindCanClaim(player);
            } else {
                messageManager.remindCantClaim(player, config.getWaitingTime(dataManager.getTimeRemaining(player.getUniqueId())));
            }
        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int newStreak, newDay, broadcastDifference = config.getInt("broadcast-difference");
        boolean broadcast;
        Player player = (Player) e.getWhoClicked();
        UUID playerId = player.getUniqueId();
        int slot = e.getRawSlot();
        int page = guiManager.isPlayerOnGui(player.getUniqueId());
        if (page == 0) return;
        e.setCancelled(true);
        int streak = dataManager.getCurrentStreak(playerId);
        int day = rewardsManager.getDay(streak);

        if (page > 1 && slot == config.getInt("gui.slots.previous_page")) {
            openGui(player, playerId, page - 1, day);
        }

        if (page < rewardsManager.getLastPage() && slot == config.getInt("gui.slots.next_page")) {
            openGui(player, playerId, page + 1, day);
        }

        // Check if the player can claim and is the claimable slot.
        if (dataManager.canClaim(playerId)
                && page == guiManager.getPageFromDay(day)
                && slot == config.getDailySlotFromIndex(streak % config.getDailySlotSize())) {
            newStreak = streak + 1;
            dataManager.setCurrentStreak(playerId, newStreak);
            dataManager.setLastClaim(playerId, System.currentTimeMillis());

            rewardsManager.runRewards(player, day);

            // Broadcast stuffs
            broadcast = broadcastDifference > 0 && newStreak % broadcastDifference == 0;
            messageManager.messageClaim(player, newStreak, broadcast);

            newDay = rewardsManager.getDay(day);
            openGui(player, playerId, guiManager.getPageFromDay(newDay), newDay);
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        guiManager.removePlayerFromGui(e.getPlayer().getUniqueId());
    }

    private void openGui(Player player, UUID playerId, int page, int day) {
        guiManager.openGui(player, page,day, dataManager.canClaim(playerId),
                config.getWaitingTime(dataManager.getTimeRemaining(playerId)));
    }

}
