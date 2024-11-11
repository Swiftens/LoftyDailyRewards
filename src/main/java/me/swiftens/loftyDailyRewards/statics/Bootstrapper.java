package me.swiftens.loftyDailyRewards.statics;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

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

    private Economy econ;

    public void initialize(LoftyDailyRewards core) {
        BukkitAudiences audiences = BukkitAudiences.create(core);
        setupEconomy();

        this.configManager = new ConfigManager(core);
        this.messageManager = new MessageManager(core, audiences);
        this.rewardsManager = new RewardsManager(core, configManager, econ, audiences);
        this.guiManager = new GuiManager(configManager, rewardsManager);
        this.dataManager = new DataManagerProvider(core);
    }

    public Economy getEconomy() {
        return this.econ;
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

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }
}
