package uk.antiperson.realfish;

import org.bukkit.scheduler.BukkitRunnable;

public class FishingTask extends BukkitRunnable {

    private final RealFish realFish;
    public FishingTask(RealFish realFish) {
        this.realFish = realFish;
    }

    @Override
    public void run() {
        for (Fisherman fisherman : realFish.getFishingManager().getFishermen()) {
            fisherman.tick();
        }
    }
}
