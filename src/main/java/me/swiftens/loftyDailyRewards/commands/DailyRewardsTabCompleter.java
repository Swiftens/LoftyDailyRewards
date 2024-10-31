package me.swiftens.loftyDailyRewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardsTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completers = new ArrayList<>();
        if (strings.length == 1) {
            completers.add("help");
            if (commandSender.hasPermission("dailyrewards.open")) completers.add("open");
            if (commandSender.hasPermission("dailyrewards.reload")) completers.add("reload");
            if (commandSender.hasPermission("dailyrewards.skip")) completers.add("skip");
            if (commandSender.hasPermission("dailyrewards.set")) completers.add("set");
        } else if (strings.length == 2) {
            if (commandSender.hasPermission("dailyrewards.skip") && strings[0].equalsIgnoreCase("skip")) {
                addPlayers(completers);
            } else if (commandSender.hasPermission("dailyrewards.set") && strings[0].equalsIgnoreCase("set")) {
                completers.add("streak");
                completers.add("higheststreak");
            }
        } else {
            addPlayers(completers);
        }

        return StringUtil.copyPartialMatches(strings[strings.length-1], completers, new ArrayList<>());
    }

    private void addPlayers(List<String> list) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
    }
}
