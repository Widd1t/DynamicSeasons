package com.widdit.dynamicSeasons.listeners;

import com.widdit.dynamicSeasons.DynamicSeasons;
import com.widdit.dynamicSeasons.SeasonManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CropGrowth implements Listener {

    private final DynamicSeasons plugin;

    public CropGrowth(DynamicSeasons plugin) {
        this.plugin = plugin;
    }

    private void fastGrowth(BlockGrowEvent event, Double chance) {

        if (chance > 1 || chance < 0) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>Chance must be between 0 and 1! Please update your config"));
        }

        chance = 1 - chance;

        if (event.getNewState().getBlockData() instanceof org.bukkit.block.data.Ageable ageable) { // check that the crop can age
            int maxAge = ageable.getMaximumAge();
            int currentAge = ageable.getAge();

            if (currentAge < maxAge && ThreadLocalRandom.current().nextDouble() < chance) { // check if the crop is not fully aged & chance
                ageable.setAge(currentAge+1);
                event.getNewState().setBlockData(ageable);
            }
        }
    }

    public void slowGrowth(BlockGrowEvent event, Double chance) {

        if (chance > 1 || chance < 0) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>Chance must be between 0 and 1! Please update your config"));
        }

        chance = 1 - chance; // 0.75 would be a 25% chance, so if the user puts 0.25 then this would return 0.75

        if (ThreadLocalRandom.current().nextDouble() < chance) { // this is the chance that crops will not grow when triggered
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        SeasonManager.Season season = plugin.getSeasonManager().getCurrentSeason(); // get season

        if (season == SeasonManager.Season.SPRING) {
            if (plugin.getConfig().getString("spring-type", "SLOW").equals("FAST")) { // if spring is fast then
                fastGrowth(event, plugin.getConfig().getDouble("spring-chance", 0)); // run the fast growth event
            } else if (plugin.getConfig().getString("spring-type", "SLOW").equals("SLOW")) { // if spring is slow then
                slowGrowth(event, plugin.getConfig().getDouble("spring-chance", 0)); // run the slow event
            }
        } else if (season == SeasonManager.Season.SUMMER) {
            if (plugin.getConfig().getString("summer-type", "FAST").equals("FAST")) { // repeat this for each season
                fastGrowth(event, plugin.getConfig().getDouble("summer-chance", 0.5));
            } else if (plugin.getConfig().getString("summer-type", "FAST").equals("SLOW")) {
                slowGrowth(event, plugin.getConfig().getDouble("summer-chance", 0.5));
            }
        } else if (season == SeasonManager.Season.AUTUMN) {
            if (plugin.getConfig().getString("autumn-type", "SLOW").equals("FAST")) {
                fastGrowth(event, plugin.getConfig().getDouble("autumn-chance", 0));
            } else if (plugin.getConfig().getString("autumn-type", "SLOW").equals("SLOW")) {
                slowGrowth(event, plugin.getConfig().getDouble("autumn-chance", 0));
            }
        } else if (season == SeasonManager.Season.WINTER) {
            if (plugin.getConfig().getString("winter-type", "SLOW").equals("FAST")) {
                fastGrowth(event, plugin.getConfig().getDouble("winter-chance", 0.25));
            } else if (plugin.getConfig().getString("winter-type", "SLOW").equals("SLOW")) {
                slowGrowth(event, plugin.getConfig().getDouble("winter-chance", 0.25));
            }
        }
    }

}
