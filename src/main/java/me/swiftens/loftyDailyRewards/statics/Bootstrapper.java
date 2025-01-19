package me.swiftens.loftyDailyRewards.statics;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.*;

public class Bootstrapper {

    private static Bootstrapper INSTANCE;

    private Bootstrapper() {}

    public static Bootstrapper getInstance() {
        if (INSTANCE == null) INSTANCE = new Bootstrapper();
        return INSTANCE;
    }

    private ConfigManager configManager;
    private RewardsManager rewardsManager;

    private DataManager dataManager;
    private GuiManager guiManager;


    public void initialize(LoftyDailyRewards core) {

        this.configManager = new ConfigManager(core);
        this.rewardsManager = new RewardsManager(core, configManager);
        this.guiManager = new GuiManager(configManager, rewardsManager);
        this.dataManager = new DataManagerProvider(core, configManager);
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RewardsManager getRewardsManager() {
        return rewardsManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

}
