package me.swiftens.loftyDailyRewards.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.swiftens.loftyDailyRewards.enums.ItemType;
import me.swiftens.loftyDailyRewards.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiManager {

    private final ConfigManager config;
    private final RewardsManager rewardsManager;
    private final Map<UUID, Integer> pageList;


    public GuiManager(ConfigManager config, RewardsManager rewardsManager) {
        this.config = config;
        this.rewardsManager = rewardsManager;
        pageList = new HashMap<>();

    }
    /*
    * Open the Daily GUI for the player
    *
    * @param player the player to open the gui to
    * @param page the page to put the gui to
    * @param day the player will claim next.
    * @param canClaim if the player can claim
    * @param time the remaining time in string format
    * @param streak the player's current streak
     */
    public void openGui(Player player, int page, int day, boolean canClaim, String time) {
        int size = config.getInt("gui.size");
        int slot;
        ItemStack item;
        List<String> dayLore;
        List<Integer> slotList = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Inventory gui = Bukkit.createInventory(null, size, config.getGuiName());

        if (page > 1) {
            slot = config.getInt("gui.slots.previous_page");
            gui.setItem(slot, config.getItem(ItemType.PREVIOUS_PAGE));
            slotList.remove(Integer.valueOf(slot));
        }

        int slotSize = config.getDailySlotSize();
        int currentDay = (page * slotSize) - (slotSize - 1);

        for (int i = 0; i < slotSize; i++) {
            slot = config.getDailySlotFromIndex(i);

            dayLore = rewardsManager.getLore(currentDay).stream()
                    .map(s -> TextUtils.translateHexCodes(PlaceholderAPI.setPlaceholders(player, s)))
                    .toList();

            // This day has been claimed already.
            if (currentDay < day) {
                item = getItem(ItemType.CLAIMED, currentDay, time, dayLore);
                gui.setItem(slot, getItem(ItemType.CLAIMED, currentDay, time, dayLore));
                // This day can not be claimed because it has not reached the streak
            } else if (currentDay > day) {
                item = getItem(ItemType.UNCLAIMED, currentDay, time, dayLore);
            } else {
                // This day can be claimed and is the day that needs to be claimed.
                if (canClaim) {
                    item = getItem(ItemType.CLAIMABLE, currentDay, time, dayLore);
                } else {
                    item = getItem(ItemType.UNCLAIMABLE, currentDay, time, dayLore);
                }
            }

            gui.setItem(slot, item);
            currentDay++;
            slotList.remove(Integer.valueOf(slot));
        }

        if (page < rewardsManager.getLastPage()) {
            slot = config.getInt("gui.slots.next_page");
            gui.setItem(slot, config.getItem(ItemType.NEXT_PAGE));
            slotList.remove(Integer.valueOf(slot));
        }

        item = config.getItem(ItemType.FRAME);
        if (!item.getType().isAir()) {
            for (int i: slotList) {
                gui.setItem(i, item);
            }
        }

        player.openInventory(gui);
        pageList.put(player.getUniqueId(), page);
    }

    /*
    * Check if a player is on the gui.
    *
    * @param playerId the player's UUID
    *
    * @return the page the player is on.
    *         0 if the player is not on the GUI
     */
    public int isPlayerOnGui(UUID playerId) {
        return pageList.getOrDefault(playerId, 0);
    }

    public void removePlayerFromGui(UUID playerId) {
        pageList.remove(playerId);
    }

    /*
    * Assumes that the day has already been calculated.
     */
    public int getPageFromDay(int day) {
        return (int) Math.ceil((double) day / config.getDailySlotSize());
    }

    private ItemStack getItem(ItemType type, int day, String time, List<String> dayLore) {
        ItemStack item = config.getItem(type, day, time);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();
        lore.addAll(dayLore);

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }


}
