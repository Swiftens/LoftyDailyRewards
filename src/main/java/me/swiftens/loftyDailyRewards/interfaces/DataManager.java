package me.swiftens.loftyDailyRewards.interfaces;

import java.sql.SQLException;
import java.util.UUID;

public interface DataManager {

    /*
    * Add the player into the database or ignore it
    * It should always be used on player join
    *
    * @param playerID the player's uuid
     */
    void setDefaultData(UUID playerId);

    /*
    * Reset the player's database with the assumption
    * that the player is already in the database.
    *
    * @param playerId the player's uuid
     */
    void resetData(UUID playerId);

    /*
    * Set the player's last claim with the assumption
    * that the player is already in the database.
    *
    * @param playerId the player's uuid
    * @param lastClaim the millisecond of the last claim.
     */
    void setLastClaim(UUID playerId, long lastClaim);

    /*
     * Get the player's last claim
     *
     * @param playerId the player's uuid
     *
     * @return the player's last claim
     */
    long getLastClaim(UUID playerId);

    /*
    * Get the player's time remaining in milliseconds
    *
    * @param playerId the player's uuid
    *
    * @return the time remaining in milliseconds
    *         -1 if the player can claim
     */
    long getTimeRemaining(UUID playerId);

    /*
    * Get the player's current streak
    *
    * @param playerId the player's uuid
    *
    * @return the player's current streak
     */
    int getCurrentStreak(UUID playerId);

    /*
    * Change the player's current streak assuming
    * the player is already in the database.
    *
    * @param playerId the player's uuid
    * @param streak the integer to set the player's streak to
     */
    void setCurrentStreak(UUID playerId, int streak);

    /*
     * Get the player's highest streak
     *
     * @param playerId the player's uuid
     *
     * @return the player's highest streak
     */
    int getHighestStreak(UUID playerId);

    /*
     * Change the player's highest streak assuming
     * the player is already in the database.
     *
     * @param playerId the player's uuid
     * @param streak the integer to set the player's highest streak to
     */
    void setHighestStreak(UUID playerId, int streak);

    /*
    * Check if the player can claim the next day
    * the instance this method is called.
    *
    * @param playerId the player's uuid
     */
    boolean canClaim(UUID playerId);

    /*
    * Migrate data from sqlite to sql and vice versa.
    * If the playerId is null, server-wide migrate is done without overrides
    * If playerId is specified, player migrate is done with override.
    *
    *
    * @param playerId the player's uuid. nullable
     */
    void migrate(UUID playerId);

    /*
    * Close the connection
     */
    void close() throws SQLException;

}
