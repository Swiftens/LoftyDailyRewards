package tech.loftydev.loftyDailyRewards.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;
import tech.loftydev.loftyDailyRewards.interfaces.DataManager;
import tech.loftydev.loftyDailyRewards.managers.GuiManager;
import tech.loftydev.loftyDailyRewards.statics.TextUtils;

import java.util.UUID;

public class DailyRewardsListener implements Listener {

    private final GuiManager guiManager;
    private final DataManager dataManager;
    private final LoftyDailyRewards core;


    public DailyRewardsListener(LoftyDailyRewards core, GuiManager guiManager, DataManager dataManager) {
        this.core = core;
        this.guiManager = guiManager;
        this.dataManager = dataManager;
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
        if (dataManager.canClaim(playerId) && page == guiManager.getPageFromStreak(streak) && slot == (streak % guiManager.getDailyPageSize())) {
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
