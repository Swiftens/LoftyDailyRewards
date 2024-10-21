package tech.loftydev.loftyDailyRewards.managers;

import tech.loftydev.loftyDailyRewards.LoftyDailyRewards;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DataManager {

    private static final String TABLE_NAME = "loftydaily_data";

    private final LoftyDailyRewards core;
    private Connection connection;

    public DataManager(LoftyDailyRewards core) {
        this.core = core;
        File file = new File(core.getDataFolder(), "data.db");
        createFile(file);

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            createTable();
        } catch (SQLException e) {
            core.getLogger().severe("Could not make a connection to the database!");
            core.getLogger().severe(e.getMessage());
        }

    }

    public void getStreak(UUID playerId) {

    }

    public void close() throws SQLException {
        if (connection != null) connection.close();
    }

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(playerId text primary key, streak integer, highest_streak integer, last_claim integer)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException e) {
            core.getLogger().severe("Could not create table for LoftyDailyRewards Database!");
            core.getLogger().severe(e.getMessage());
        }
    }

    private void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                core.getLogger().warning("Could not create 'data.db' for KosmosCore!");
                core.getLogger().warning("If file already exists, ignore this message.");
            }
        }
    }

}
