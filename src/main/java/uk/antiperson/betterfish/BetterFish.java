package uk.antiperson.betterfish;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterFish extends JavaPlugin {

    private FishingManager fishingManager;

    @Override
    public void onEnable() {
        fishingManager = new FishingManager(this);
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        new FishingTask(this).runTaskTimer(this,1,1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FishingManager getFishingManager() {
        return fishingManager;
    }
}
