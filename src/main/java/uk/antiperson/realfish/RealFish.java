package uk.antiperson.realfish;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RealFish extends JavaPlugin implements CommandExecutor {

    private FishingManager fishingManager;

    @Override
    public void onEnable() {
        fishingManager = new FishingManager(this);
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        new FishingTask(this).runTaskTimer(this,1,1);
        getCommand("realfish").setExecutor(this);
        new Metrics(this, 16057);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FishingManager getFishingManager() {
        return fishingManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("RealFish v" + getDescription().getVersion());
        sender.sendMessage("GitHub: https://github.com/Nathat23/RealFish");
        sender.sendMessage("Discord: https://discord.gg/GadyA9j");
        return false;
    }
}
