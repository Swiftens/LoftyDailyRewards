package tech.loftydev.loftyDailyRewards.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;
import tech.loftydev.loftyDailyRewards.enums.ItemType;
import tech.loftydev.loftyDailyRewards.statics.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GuiManager {

    private final LoftyDailyRewards core;
    private final Map<ItemType, ItemStack> items;
    private final Map<UUID, Integer> pageList;

    private List<Integer> slots;
    private int lastPage;

    public GuiManager(LoftyDailyRewards core) {
        this.core = core;
        items = new HashMap<>();
        pageList = new HashMap<>();

        cacheItems();
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
    public void openGui(Player player, int page, int day, boolean canClaim, String time, int streak) {
        int size = core.getConfig().getInt("gui.size");
        int slot;
        List<Integer> slotList = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Inventory gui = Bukkit.createInventory(null, size, colorize(core.getConfig().getString("gui.name")));

        if (page > 1) {
            slot = core.getConfig().getInt("gui.slots.previous_page");
            gui.setItem(slot, items.get(ItemType.PREVIOUS_PAGE));
            slotList.remove(slot);
        }

        // TODO: Do the days bruh, I'm so lost
        int slotSize = getDailyPageSize();
        int currentDay = (page * slotSize) - (slotSize - 1);

        for (int i = 0; i < slotSize; i++) {
            slot = slots.get(i);

            // This day has been claimed already.
            if (currentDay <= streak) {
                gui.setItem(slot, getItem(ItemType.CLAIMED, currentDay, time));
                // This day can not be claimed because it has not reached the streak
            } else if (currentDay > streak + 1) {
                gui.setItem(slot, getItem(ItemType.UNCLAIMED, currentDay, time));
            } else {
                // This day can be claimed and is the day that needs to be claimed.
                if (canClaim) {
                    gui.setItem(slot, getItem(ItemType.CLAIMABLE, currentDay, time));
                } else {
                    gui.setItem(slot, getItem(ItemType.UNCLAIMABLE, currentDay, time));
                }
            }

            currentDay++;
            slotList.remove(slot);
        }


        if (page < getLastPage()) {
            slot = core.getConfig().getInt("gui.slots.next_page");
            gui.setItem(slot, items.get(ItemType.NEXT_PAGE));
            slotList.remove(slot);
        }

        if (!getItem(ItemType.FRAME).getType().isAir()) {
            for (int i: slotList) {
                gui.setItem(i, getItem(ItemType.FRAME));
            }
        }

        player.openInventory(gui);
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

    public int getDailyPageSize() {
        return slots.size();
    }

    public int getLastPage() {
        return this.lastPage;
    }

    public int getPageFromStreak(int streak) {
        streak += 1;
        return (int) Math.ceil(((double) streak/ getDailyPageSize()));
    }

    private ItemStack getItem(ItemType type, int day, String time) {
        ItemStack item = new ItemStack(getItem(type));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("{day}", String.valueOf(day)));
        meta.setLore(getLore(type, day, time));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getItem(ItemType type) {
        return items.get(type);
    }

    public void cacheItems() {
        lastPage = (int) ((double) core.getConfig().getConfigurationSection("days").getKeys(false).size() / pageList.size());
        slots = core.getConfig().getIntegerList("gui.slots.daily");

        ItemStack item;
        ItemMeta meta;
        for (ItemType type: ItemType.values()) {
            item = new ItemStack(Material.valueOf(core.getConfig().getString(type.getKey() + ".item")), 1);
            meta = item.getItemMeta();
            meta.setDisplayName(colorize(core.getConfig().getString(type.getKey() + ".name")));
            item.setItemMeta(meta);

            items.put(type, item);
        }

    }

    private List<String> getLore(ItemType type, int day, String time) {
        List<String> lore =  Stream.concat(
                core.getConfig().getStringList(type.getKey() + ".lore").stream(),
                core.getConfig().getStringList("days." + day + ".lore").stream()
        ).toList();

        return lore.stream()
                .map(l -> colorize(l)
                        .replace("{time}", time)
                        .replace("{day}", String.valueOf(day)))
                .toList();
    }

    private String colorize(String text) {
        return TextUtils.translateHexCodes(text);
    }

}
