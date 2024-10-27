package me.swiftens.loftyDailyRewards.managers;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.interfaces.DataManager;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManagerProvider implements DataManager {

    private static final String TABLE_NAME = "loftydaily_data";

    private final LoftyDailyRewards core;
    private Connection connection;

    // Cache getLastClaim because it can be called quite a lot.
    private final Map<UUID, Long> lastClaimCache;


    public DataManagerProvider(LoftyDailyRewards core) {
        this.core = core;
        this.lastClaimCache = new HashMap<>();
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

    @Override
    public void setDefaultData(UUID playerId) {
        String query = "INSERT OR IGNORE INTO " + TABLE_NAME + " (playerId, streak, highest_streak, last_claim) values (?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, playerId.toString());
            statement.setInt(2, 0);
            statement.setInt(3, 0);
            statement.setLong(4, 0L);
            statement.executeUpdate();
        } catch (SQLException e) {
            core.getLogger().severe("Could not add default data for player! PlayerID: " + playerId);
            core.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void resetData(UUID playerId) {
        String query = "UPDATE " + TABLE_NAME + " set streak = ?, highest_streak = ?, last_claim = ?  where playerId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, 0);
            statement.setInt(2, 0);
            statement.setLong(3, 0L);
            statement.setString(4, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            core.getLogger().severe("Could not reset player's data! PlayerID: " + playerId);
            core.getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void setLastClaim(UUID playerId, long lastClaim) {
        updateTable("last_claim", lastClaim, playerId);
        lastClaimCache.put(playerId, lastClaim);
    }

    @Override
    public long getLastClaim(UUID playerId) {
        return lastClaimCache.computeIfAbsent(playerId, id -> getValue("last_claim", id));
    }

    @Override
    public long getTimeRemaining(UUID playerId) {
        if (canClaim(playerId)) return -1;
        return getLastClaim(playerId) + 86400000 - System.currentTimeMillis();
    }

    @Override
    public int getCurrentStreak(UUID playerId) {
        return (int) getValue("streak", playerId);
    }

    @Override
    public void setCurrentStreak(UUID playerId, int streak) {
        updateTable("streak", streak, playerId);

        // Check if the streak is higher than the highest, then we set the highest to that.
        if (streak > getHighestStreak(playerId)) setHighestStreak(playerId, streak);
    }

    @Override
    public int getHighestStreak(UUID playerId) {
        return (int) getValue("highest_streak", playerId);
    }

    @Override
    public void setHighestStreak(UUID playerId, int streak) {
        updateTable("highest_streak", streak, playerId);
    }

    @Override
    public boolean canClaim(UUID playerId) {
        long difference = System.currentTimeMillis() - getLastClaim(playerId);

        // If the difference is over 2 days, then the streak is reset.
        if (difference >= 172800000) {
            setCurrentStreak(playerId, 0);
            return true;
        }

        // If the difference is over 1 day, then they can claim.
        return difference >= 86400000;
    }


    @Override
    public void close() throws SQLException {
        if (connection != null) connection.close();
    }


    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(playerId text primary key, streak integer, highest_streak integer, last_claim integer)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
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
                core.getLogger().warning("Could not create 'data.db' for LoftyDailyRewards!");
                core.getLogger().warning("If file already exists, ignore this message.");
            }
        }
    }

    private void updateTable(String column, long value, UUID playerId) {
        String query = "UPDATE " + TABLE_NAME + " set " + column + " = ? where playerId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, value);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            core.getLogger().severe("Could not set player's " + column + "! PlayerID: " + playerId );
            core.getLogger().severe(e.getMessage());
        }

    }

    private long getValue(String column, UUID playerId) {
        String query = "SELECT " + column + " FROM " + TABLE_NAME + " WHERE playerId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong(column);
            }
            return 0;
        } catch(SQLException e) {
            core.getLogger().severe("Could not get player's " + column + "! PlayerID: " + playerId );
            core.getLogger().severe(e.getMessage());
            return 0;
        }

    }

}
