package com.widdit.dynamicSeasons;

import com.widdit.dynamicSeasons.commands.SeasonCommand;
import com.widdit.dynamicSeasons.listeners.CropGrowth;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DynamicSeasons extends JavaPlugin {

    private SeasonManager seasonManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.seasonManager = new SeasonManager(this); // initialise SeasonManager
        seasonManager.startCalendar();
        seasonManager.checkConfigForInvalidStartSeason();

        getServer().getPluginManager().registerEvents(new CropGrowth(this), this); // initialise event

        SeasonCommand seasonCommand = new SeasonCommand(this); // declare command class

        if (this.getCommand("season") != null) {
            Objects.requireNonNull(this.getCommand("season")).setExecutor(seasonCommand);
            Objects.requireNonNull(this.getCommand("season")).setTabCompleter(seasonCommand); // requireNonNull prevents crashes
        } // enable command without throwing error if class cant be found

        getLogger().info("Thank you for using Widdit's Dynamic Seasons plugin! :)");
        getLogger().info("IF YOU PAID FOR THIS PLUGIN YOU WERE SCAMMED AND SHOULD DEMAND A REFUND!!");
        getLogger().info("This plugin's source code is freely viewable on github at https://github.com/Widd1t/DynamicSeasons");
    }

    @Override
    public void onDisable() {
        if (this.seasonManager != null) {
            this.seasonManager.stopCalendar();
        }
    }

    public SeasonManager getSeasonManager() {
        return this.seasonManager;
    }
}
