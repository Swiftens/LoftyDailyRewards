package me.swiftens.loftyDailyRewards.statics;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class Bootstrapper {

    private static Bootstrapper INSTANCE;

    private Bootstrapper() {}

    public static Bootstrapper getInstance() {
        if (INSTANCE == null) INSTANCE = new Bootstrapper();
        return INSTANCE;
    }

    private ConfigManager configManager;
    private RewardsManager rewardsManager;
    private MessageManager messageManager;

    private DataManager dataManager;
    private GuiManager guiManager;

    public void initialize(LoftyDailyRewards core) {
        BukkitAudiences audiences = BukkitAudiences.create(core);

        this.configManager = new ConfigManager(core);
        this.messageManager = new MessageManager(core, audiences);
        this.rewardsManager = new RewardsManager(core, configManager);
        this.guiManager = new GuiManager(configManager, rewardsManager);
        this.dataManager = new DataManagerProvider(core);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RewardsManager getRewardsManager() {
        return rewardsManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
