package uk.antiperson.betterfish;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class FishingManager {

    private final Set<Fisherman> fishermen;
    private final BetterFish betterFish;

    public FishingManager(BetterFish betterFish) {
        this.betterFish = betterFish;
        this.fishermen = new HashSet<>();
    }

    public Set<Fisherman> getFishermen() {
        return fishermen;
    }

    public Fisherman getFisherman(Player player) {
        for (Fisherman fisherman : getFishermen()) {
            if (fisherman.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return fisherman;
            }
        }
        return null;
    }

    public void addFisherman(Fisherman fisherman) {
        fishermen.add(fisherman);
    }

    public void removeFisherman(Fisherman fisherman) {
        fishermen.remove(fisherman);
    }
}
