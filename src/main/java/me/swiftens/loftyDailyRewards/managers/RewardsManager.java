package me.swiftens.loftyDailyRewards.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.utils.TextUtils;
import me.swiftens.loftyDailyRewards.utils.YamlAccess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RewardsManager {

    private final YamlAccess rewards;
    private final ConfigManager config;

    private int totalDays;
    private int lastPage;

    public RewardsManager(LoftyDailyRewards core, ConfigManager config) {
        this.config = config;
        this.rewards = new YamlAccess(core, "rewards.yml");
        rewards.saveDefaultConfig();

        totalDays = rewards.getFile().getConfigurationSection("days").getKeys(false).size();
        lastPage = (int) Math.ceil((double) totalDays / config.getDailySlotSize());
    }

    public List<String> getLore(int day) {
        return rewards.getFile().getStringList("days."+day+".lore").stream().map(TextUtils::translateHexCodes).toList();
    }

    public void runRewards(Player player, int day) {
        for (String command: rewards.getFile().getStringList("days."+day+".rewards")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player,command.replace("{player}", player.getName())));
        }
    }

    public int getDay(int streak) {
        int day = streak + 1 % totalDays;
        if (day == 0) day = totalDays;

        return day;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void reload() {
        rewards.reloadFile();

        totalDays = rewards.getFile().getConfigurationSection("days").getKeys(false).size();
        lastPage = (int) Math.ceil((double) totalDays / config.getDailySlotSize());
    }

}
