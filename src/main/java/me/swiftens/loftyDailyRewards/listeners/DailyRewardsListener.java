package me.swiftens.loftyDailyRewards.listeners;

import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.managers.MessageManager;
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

    private final GuiManager guiManager;
    private final DataManager dataManager;
    private final LoftyDailyRewards core;
    private final MessageManager messageManager;


    public DailyRewardsListener(LoftyDailyRewards core, MessageManager messageManager, GuiManager guiManager, DataManager dataManager) {
        this.core = core;
        this.messageManager = messageManager;
        this.guiManager = guiManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        dataManager.setDefaultData(player.getUniqueId());

        if (player.hasPermission("dailyrewards.open")) {
            if (dataManager.canClaim(player.getUniqueId())) {
                messageManager.sendMessage(player, MessageKeys.REMINDER_CAN_CLAIM, null);
            } else {
                messageManager.sendMessage(player, MessageKeys.REMINDER_CANT_CLAIM,
                        TextUtils.getTimeRemaining(dataManager.getLastClaim(player.getUniqueId()) + 86400000 - System.currentTimeMillis()));
            }
        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID playerId = player.getUniqueId();
        int slot = e.getRawSlot();
        int page = guiManager.isPlayerOnGui(player.getUniqueId());
        if (page == 0) return;
        e.setCancelled(true);
        int streak = dataManager.getCurrentStreak(playerId);

        if (page > 1 && slot == core.getConfig().getInt("gui.slots.previous_page")) {
            openGui(player, playerId, page - 1, streak);
        }

        if (page < guiManager.getLastPage() && slot == core.getConfig().getInt("gui.slots.next_page")) {
            openGui(player, playerId, page + 1, streak);
        }

        // Check if the player can claim and is the claimable slot.t
        if (dataManager.canClaim(playerId) && page == guiManager.getPageFromStreak(streak) && slot == guiManager.getDailySlotFromIndex(streak % guiManager.getDailyPageSize())) {
            int newStreak = streak + 1;
            dataManager.setCurrentStreak(playerId, newStreak);
            dataManager.setLastClaim(playerId, System.currentTimeMillis());

            CommandSender console = Bukkit.getConsoleSender();
            for (String command: core.getConfig().getStringList("days." + newStreak + ".rewards")) {
                Bukkit.dispatchCommand(console, command.replace("{player}", player.getName()));
            }

            openGui(player, playerId, guiManager.getPageFromStreak(newStreak), newStreak);
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        guiManager.removePlayerFromGui(e.getPlayer().getUniqueId());
    }

    private void openGui(Player player, UUID playerId, int page, int streak) {
        guiManager.openGui(player, page,streak + 1, dataManager.canClaim(playerId),
                TextUtils.getTimeRemaining(dataManager.getLastClaim(playerId) + 86400000 - System.currentTimeMillis()), streak);
    }

}
