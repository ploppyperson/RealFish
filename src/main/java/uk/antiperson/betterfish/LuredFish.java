package uk.antiperson.betterfish;

import org.bukkit.Location;
import org.bukkit.entity.Fish;
import org.bukkit.util.Vector;

public class LuredFish {

    private final Fish fish;
    private final FishBobber bobber;
    private final double speed;
    private Vector movementVector;
    private Vector bobbingVector;

    public LuredFish(FishBobber bobber, Fish fish, double speed) {
        this.bobber = bobber;
        this.fish = fish;
        this.speed = speed;
    }

    public Fish getFish() {
        return fish;
    }

    public Location getLocation() {
        return getFish().getLocation();
    }

    public FishBobber getBobber() {
        return bobber;
    }

    public void tick() {
        if (getBobber().getOwner().getFishingState() != Fisherman.FishingState.LURING) {
            if (getBobber().getOwner().getFishingState() == Fisherman.FishingState.HOOKED) {
                return;
            }
            throw new UnsupportedOperationException("Fish bobber needs to be luring to lure a fish.");
        }
        Location fishLoc = getFish().getLocation();
        Location hookLoc = getBobber().getHook().getLocation().subtract(0,0.3,0);
        if (movementVector == null) {
            Vector fish = fishLoc.toVector();
            Vector hook = hookLoc.toVector();
            movementVector = fish.clone().multiply(-1).add(hook).multiply(speed);
            fishLoc.setDirection(movementVector);
            getFish().teleport(fishLoc);
        }
        getFish().setVelocity(movementVector);
        if (fishLoc.distance(hookLoc) > 0.2) {
            return;
        }
        getBobber().getOwner().setFishingState(Fisherman.FishingState.HOOKED);
    }
}
