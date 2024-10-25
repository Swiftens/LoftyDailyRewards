package me.swiftens.loftyDailyRewards.managers;

import me.swiftens.loftyDailyRewards.LoftyDailyRewards;
import me.swiftens.loftyDailyRewards.enums.MessageKeys;
import me.swiftens.loftyDailyRewards.utils.TextUtils;
import me.swiftens.loftyDailyRewards.utils.YamlAccess;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final LoftyDailyRewards core;
    private final YamlAccess messages;
    private final Map<MessageKeys, String> messageCache;
    private String prefix;

    public MessageManager(LoftyDailyRewards core) {
        this.core = core;
        this.messages = new YamlAccess(core, "messages.yml");
        messages.saveDefaultConfig();

        this.messageCache = new HashMap<>();
        cacheMessage();
    }

    /*
    * Send a message to a player
    *
    * @param sender the sender or player to send the message to
    * @param key the messagekey that corresponds the message to
    * @param time the time remaining for message.
     */
    public void sendMessage(CommandSender sender, MessageKeys key, String time) {
        String message = prefix;

        // No need to send a message if the cache is empty for the given key
        String cachedMessage = messageCache.get(key);
        if (StringUtils.isEmpty(cachedMessage)) {
            return;
        }

        message += cachedMessage;

        if (time != null) {
            message = message.replace("{time}", time);
        }

        // Send the message to the sender
        sender.sendMessage(message);
    }



    public void reload() {
        messages.reloadFile();
        cacheMessage();
    }


    private void cacheMessage() {
        // Default Strings are always empty rather than null
        prefix = TextUtils.translateHexCodes(messages.getFile().getString("prefix", ""));
        for (MessageKeys keys: MessageKeys.values()) {
            messageCache.put(keys, TextUtils.translateHexCodes(messages.getFile().getString(keys.getKey())));
        }
    }



}
