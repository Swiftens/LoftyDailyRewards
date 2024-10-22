package tech.loftydev.loftyDailyRewards.statics;

import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;
import tech.loftydev.loftyDailyRewards.interfaces.DataManager;
import tech.loftydev.loftyDailyRewards.managers.DataManagerProvider;

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

        this.dataManager = new DataManagerProvider(core);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
