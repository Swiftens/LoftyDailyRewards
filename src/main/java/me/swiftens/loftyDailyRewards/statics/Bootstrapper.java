package me.swiftens.loftyDailyRewards.statics;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;
import me.swiftens.loftyDailyRewards.managers.DataManagerProvider;
import me.swiftens.loftyDailyRewards.managers.GuiManager;
import me.swiftens.loftyDailyRewards.managers.MessageManager;

public class Bootstrapper {

    private static Bootstrapper INSTANCE;

    private Bootstrapper() {}

    public static Bootstrapper getInstance() {
        if (INSTANCE == null) INSTANCE = new Bootstrapper();
        return INSTANCE;
    }

    private DataManager dataManager;
    private GuiManager guiManager;
    private MessageManager messageManager;

    public void initialize(LoftyDailyRewards core) {
        core.saveDefaultConfig();

        this.messageManager = new MessageManager(core);
        this.dataManager = new DataManagerProvider(core);
        this.guiManager = new GuiManager(core);
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
