package tech.loftydev.loftyDailyRewards.statics;

import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;
import tech.loftydev.loftyDailyRewards.interfaces.DataManager;
import tech.loftydev.loftyDailyRewards.managers.DataManagerProvider;
import tech.loftydev.loftyDailyRewards.managers.GuiManager;

public class Bootstrapper {

    private static Bootstrapper INSTANCE;

    private Bootstrapper() {}

    public static Bootstrapper getInstance() {
        if (INSTANCE == null) INSTANCE = new Bootstrapper();
        return INSTANCE;
    }

    private DataManager dataManager;
    private GuiManager guiManager;

    public void initialize(LoftyDailyRewards core) {
        core.saveDefaultConfig();

        this.dataManager = new DataManagerProvider(core);
        this.guiManager = new GuiManager(core);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
