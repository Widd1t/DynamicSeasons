package com.widdit.dynamicSeasons.commands;

import com.widdit.dynamicSeasons.DynamicSeasons;
import com.widdit.dynamicSeasons.SeasonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SeasonCommand implements CommandExecutor, TabCompleter {

    private final DynamicSeasons plugin;

    public SeasonCommand(DynamicSeasons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) { // does not accept /season, but will accept /season get or /season set
            sender.sendMessage("§cUsage: /season <get|set>");
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("get")) {
            if (!sender.hasPermission("seasons.use")) {
                sender.sendMessage("§cYou don't have permission to do that!");
                return true;
            }

            SeasonManager.Season current = plugin.getSeasonManager().getCurrentSeason();
            sender.sendMessage("§e[DynamicSeasons] §aThe current season is " + current + "§a.");
            return true;
        }

        if (subCommand.equals("set")) {
            if (!sender.hasPermission("seasons.admin")) {
                sender.sendMessage("§cYou don't have permission to change the season.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§cUsage: /season set <spring|summer|autumn|winter|next>");
                return true;
            }

            String inputSeason = args[1].toUpperCase();
            if (inputSeason.equals("FALL")) {
                inputSeason = "AUTUMN";
            } // catches anyone who calls autumn fall
            if (inputSeason.equals("NEXT")) {
                plugin.getSeasonManager().transitionSeason(); // use existing season transition to go to the next season
                return true;
            } else try {
                SeasonManager.Season targetSeason = SeasonManager.Season.valueOf(inputSeason);
                plugin.getSeasonManager().setCurrentSeason(targetSeason); // set current season to input season
                return true;
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid season! Choose spring, summer, autumn, or winter.");
                return true;
            }
        }

        if (subCommand.equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§e[DynamicSeasons] §aConfig Reloaded!");
            return true;
        }

        sender.sendMessage("§cUnknown command, try again?"); // catch all in case the user somehow gets around all that
        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("seasons.use") && "get".startsWith(args[0].toLowerCase())) {
                completions.add("get");
            }
            if (sender.hasPermission("seasons.admin") && "set".startsWith(args[0].toLowerCase())) {
                completions.add("set");
            }
            if (sender.hasPermission("seasons.admin") && "reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("seasons.admin")) return completions;

            for (SeasonManager.Season season : SeasonManager.Season.values()) {
                String name = season.name().toLowerCase();
                if (name.startsWith(args[1].toLowerCase())) {
                    completions.add(name);
                }
            }

            completions.add("next");
        }

        return completions;
    }
}
