package tech.loftydev.loftyDailyRewards.statics;

import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;
import tech.loftydev.loftyDailyRewards.managers.DataManager;

public class Bootstrapper {

    private static Bootstrapper INSTANCE;

    private Bootstrapper() {}

    public static Bootstrapper getInstance() {
        if (INSTANCE == null) INSTANCE = new Bootstrapper();
        return INSTANCE;
    }

    private DataManager dataManager;

    public void initialize(LoftyDailyRewards core) {
        core.saveDefaultConfig();

        this.dataManager = new DataManager(core);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
