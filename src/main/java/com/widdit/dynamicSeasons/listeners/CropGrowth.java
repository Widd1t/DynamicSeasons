package com.widdit.dynamicSeasons.listeners;

import com.widdit.dynamicSeasons.DynamicSeasons;
import com.widdit.dynamicSeasons.SeasonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CropGrowth implements Listener {

    private final DynamicSeasons plugin;

    public CropGrowth(DynamicSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        SeasonManager.Season season = plugin.getSeasonManager().getCurrentSeason(); // get season

        if (season == SeasonManager.Season.SPRING) { // spring has default minecraft growth for now
            event.setCancelled(false);
        } else if (season == SeasonManager.Season.SUMMER) {
            if (event.getNewState().getBlockData() instanceof org.bukkit.block.data.Ageable ageable) { // check that the crop can age
                int maxAge = ageable.getMaximumAge();
                int currentAge = ageable.getAge();

                if (currentAge < maxAge && ThreadLocalRandom.current().nextDouble() < 0.5) { // check if the crop is not fully aged
                    ageable.setAge(currentAge+1);
                    event.getNewState().setBlockData(ageable);
                }
            }
        } else if (season == SeasonManager.Season.AUTUMN) { // autumn also has default minecraft growth for now
            event.setCancelled(false);
        } else if (season == SeasonManager.Season.WINTER) {
            if (ThreadLocalRandom.current().nextDouble() < 0.75) { // crops will grow at a rate of 75% during winter
                event.setCancelled(true);
            }
        }
    }

}
