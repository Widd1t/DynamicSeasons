package com.widdit.dynamicSeasons;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class SeasonManager {

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER;

        private static final Season[] values = values(); // private static copy to avoid copying array each time next() is called

        public Season next() {
            return values[(this.ordinal() + 1) % values.length]; // returns the next season in the array (so if spring summer, if summer autumn etc)
        }
    }

    private final DynamicSeasons plugin; // reference main class
    private Season currentSeason;

    public SeasonManager(DynamicSeasons plugin) { // pass main class through constructor
        this.plugin = plugin;
        final String startingSeason = plugin.getConfig().getString("starting-season", "SPRING");
        this.currentSeason = Season.valueOf(startingSeason); // first ever season is spring unless config says so
    }

    public void checkConfigForInvalidStartSeason() {
        if (!plugin.getConfig().getString("starting-season", "SPRING").matches("SPRING|SUMMER|AUTUMN|WINTER")) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>You haven't entered a valid value in your starting season, please update your config!"));
            plugin.getLogger().log(Level.SEVERE, "You haven't entered a valid value in your starting season, please update your config!");
        }
    }

    private BukkitTask bukkitLoop;

    public void startCalendar() {
        this.bukkitLoop = new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                if (world == null) return;

                long days = world.getFullTime() / 24000;
                // 24,000 ticks in a minecraft day, getFullTime() gets the amount of ticks happened ever, therefore this is how many days passed
                // is long instead of int or double because the number can be very very big if the world is old
                if (days % plugin.getConfig().getInt("days-per-season", 7) == 0 && world.getTime() <= 20) { // gets days per season from config if no value then 7
                    // this happens once per in game week
                    transitionSeason();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // run once per second
    }

    public void stopCalendar() {
        if (this.bukkitLoop != null) {
            bukkitLoop.cancel();
        }
    }

    public void transitionSeason() {
        this.currentSeason = this.currentSeason.next();
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<yellow>The season is now " + currentSeason.name() + "!"));

        World world = Bukkit.getWorld("world");
        if (world != null && currentSeason == Season.WINTER) { // winters are harsh
            world.setStorm(true); // stormy during winter (also, in listeners.CropGrowth i made crops grow slower
        }
    }

    public Season getCurrentSeason() {
        return this.currentSeason;
    }

    public void setCurrentSeason(Season season) {
        this.currentSeason = season;
    }

}
