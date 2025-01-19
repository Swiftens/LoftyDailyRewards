package me.swiftens.loftyDailyRewards.configs;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Getter
@Configuration
public class MessageConfig {

    @Comment("Considering MiniMessage: If the message stays bold, I recommend to use &r or just use minimessage instead.")
    private String prefix = "&7[&#c9e9f6&lDaily Rewards&r&7] ";

    @Comment({"", "Most messages work with placeholders.", "{player} - The player's username", "{display_name} - The player's display name", "{streak} - The player's current streak"})
    private String claimBroadcast = "&e{player} has logged on for {streak} days straight!";
    private String claimMessage = "&eYou claimed your {streak} day rewards!";

    @Comment({"", "These are the reminders sent to the player upon logging in.", "{time} - The time remaining"})
    private String loginCantClaim = "&aYou can claim your rewards in &c{time}&a!";

    @Comment({"","If the player can claim, they are also reminded every \"reminder-interval\" in minutes that they can claim."})
    private String canClaim = "&aYou can claim your daily reward!";

    @Comment("")
    private Leaderboard leaderboard = new Leaderboard();

    @Comment("")
    public Commands commands = new Commands();

    @Getter
    @Configuration
    public static class Leaderboard {
        private String header = "&f&m-----&r[ &eDaily Rewards Leaderboard &f]&m-----";
        private String item = "&f{rank}. {player}: {streak} days";

        private Leaderboard() {}
    }

    @Getter
    @Configuration
    public static class Commands {
        private String configReloaded = "&eConfig files reloaded!";
        private String migrateSuccessful = "&eSuccessfully migrated database.";
        private String skipSuccessful = "&eSkipped waiting time for player.";
        private SetMessage streakSetSuccessful = new SetMessage(
                "&eChanged player's current streak to {streak}.",
                "&eYour current streak was set to {streak}."
        );
        private SetMessage highestStreakSetSuccessful = new SetMessage(
                "&eChanged player's highest streak to {streak}.",
                "&eYour highest streak was set to {streak}."
        );
        private String playerOnly = "&cThis command is for players only!";
        private String invalidAmount = "&cInvalid amount set! Must be a positive integer.";
        private String noPermission = "&cYou do not have the right permissions to run this command!";
        private String invalidPlayer = "&cInvalid player!";

        private Commands() {}
    }

    @Getter
    @Configuration
    public static class SetMessage {
        private String message;
        private String notify;

        public SetMessage(String message, String notify) {
            this.message = message;
            this.notify = notify;
        }

        private SetMessage() {}
    }

}
