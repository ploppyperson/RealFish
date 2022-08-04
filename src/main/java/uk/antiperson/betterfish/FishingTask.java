package uk.antiperson.betterfish;

import org.bukkit.scheduler.BukkitRunnable;

public class FishingTask extends BukkitRunnable {

    private final BetterFish betterFish;
    public FishingTask(BetterFish betterFish) {
        this.betterFish = betterFish;
    }

    @Override
    public void run() {
        for (Fisherman fisherman : betterFish.getFishingManager().getFishermen()) {
            fisherman.tick();
        }
    }
}
