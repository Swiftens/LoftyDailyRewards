package me.swiftens.loftyDailyRewards.managers;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.enums.ItemType;
import me.swiftens.loftyDailyRewards.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final LoftyDailyRewards core;

    private List<Integer> dailySlots;
    private final Map<ItemType, ItemStack> itemStacks;

    public ConfigManager(LoftyDailyRewards core) {
        this.core = core;
        core.saveDefaultConfig();
        itemStacks = new HashMap<>();
        init();
    }

    public int getInt(String path) {
        return core.getConfig().getInt(path);
    }

    public int getDailySlotFromIndex(int index) {
        return dailySlots.get(index);
    }

    public int getDailySlotSize() {
        return dailySlots.size();
    }

    public String getDatabaseString(String path) {
        return core.getConfig().getString("database." + path);
    }

    public String getWaitingTime(long millis) {
        if (millis < 1) {
            return TextUtils.translateHexCodes(core.getConfig().getString("time-remaining.can-claim"));
        }

        String format = TextUtils.translateHexCodes(core.getConfig().getString("time-remaining.waiting"));

        long seconds = millis / 1000L;
        long minutes = 0L;
        while (seconds > 60L) {
            seconds -=60L;
            minutes++;
        }
        long hours = 0L;
        while (minutes > 60L) {
            minutes -=60L;
            hours++;
        }
        return format.replace("%h", String.valueOf(hours))
                .replace("%m", String.valueOf(minutes))
                .replace("%s", String.valueOf(seconds));
    }

    public String getGuiName() {
        return TextUtils.translateHexCodes(core.getConfig().getString("gui.name"));
    }

    public ItemStack getItem(ItemType type) {
        return getItem(type, 0, null);
    }

    public ItemStack getItem(ItemType type, int day, String time) {
        ItemStack item = new ItemStack(itemStacks.get(type));
        ItemMeta meta = item.getItemMeta();

        String name = meta.getDisplayName();
        if (time != null) name = name.replace("{time}", time);

        meta.setDisplayName(name.replace("{day}", String.valueOf(day)));

        List<String> lore = meta.getLore();
        if (lore != null) lore.replaceAll(s -> s.replace("{day}", String.valueOf(day)).replace("{time}", time));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void init() {
        core.getConfig().options().copyDefaults(true);
        saveAndReload();
        int version = getInt("file-version");
        if (version < 2) {
            core.getConfig().setComments("update-remind", List.of("Whether to be reminded when an update is uploaded"));
            version++;
        }
        if (version < 3) {
            core.getConfig().setComments("database", List.of("Changing this requires a restart"));
            core.getConfig().setComments("database.type", List.of("\"sqlite\" or \"mysql\""));
            core.getConfig().setComments("database.table_prefix", List.of("These values are only needed to be set up if the database type is sql"));
            version++;
        }
        core.getConfig().set("file-version", version);
        core.saveConfig();

        dailySlots = core.getConfig().getIntegerList("gui.slots.daily");

        itemStacks.clear();
        ItemStack item;
        ItemMeta meta;
        ConfigurationSection section;
        List<String> lore;

        for (ItemType type: ItemType.values()) {
            section = core.getConfig().getConfigurationSection(type.getKey());
            item = new ItemStack(Material.valueOf(section.getString("item")), 1);
            meta = item.getItemMeta();
            meta.setDisplayName(colorize(section.getString("name")));
            if (section.contains("model-data")) meta.setCustomModelData(section.getInt("model-data"));

            lore = section.getStringList("lore");
            lore.replaceAll(this::colorize);
            meta.setLore(lore);
            item.setItemMeta(meta);

            itemStacks.put(type, item);
        }

    }

    public void reload() {
        core.reloadConfig();
        init();
    }

    private String colorize(String text) {
        return TextUtils.translateHexCodes(text);
    }

    private void saveAndReload() {
        core.saveConfig();
        core.reloadConfig();
    }

}
