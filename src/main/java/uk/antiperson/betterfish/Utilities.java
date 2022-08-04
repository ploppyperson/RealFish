package uk.antiperson.betterfish;

import org.bukkit.Material;
import org.bukkit.entity.Fish;

public class Utilities {

    public static Material getFishItem(Fish fish) {
        switch (fish.getType()) {
            case COD:
                return Material.COD;
            case SALMON:
                return Material.SALMON;
            case PUFFERFISH:
                return Material.PUFFERFISH;
            case TROPICAL_FISH:
                return Material.TROPICAL_FISH;
        }
        return Material.STICK;
    }
}
