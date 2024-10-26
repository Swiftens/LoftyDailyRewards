package me.swiftens.loftyDailyRewards.enums;

public enum MessageKeys {

    REMINDER_CANT_CLAIM("login-cant-claim"),
    REMINDER_CAN_CLAIM("can-claim"),
    COMMAND_SKIP_SUCCESSFUL("commands.skip-successful"),
    COMMAND_STREAK_SUCCESSFUL("commands.streak-set-successful"),
    COMMAND_HIGHEST_STREAK_SUCCESSFUL("commands.highest-streak-set-successful"),
    COMMAND_PLAYER_ONLY("commands.player-only"),
    COMMAND_INVALID_PLAYER("commands.invalid-player"),
    COMMAND_NO_PERMISSION("commands.no-permission"),
    COMMAND_INVALID_AMOUNT("commands.invalid-amount"),
    COMMAND_CONFIG_RELOADED("commands.config-reloaded");


    private final String key;

    MessageKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}