package uk.antiperson.betterfish;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Fisherman {

    private final Player player;
    private FishBobber fishBobber;
    private FishingState fishingState;

    public Fisherman(Player player, FishHook fishHook) {
        this.player = player;
        this.fishBobber = new FishBobber(this, fishHook);
    }

    public Player getPlayer() {
        return player;
    }

    public FishBobber getFishBobber() {
        return fishBobber;
    }

    public FishingState getFishingState() {
        return fishingState;
    }

    public void setFishingState(FishingState fishingState) {
        this.fishingState = fishingState;
        System.out.println(getFishingState() + "," + getFishBobber().getWait());
    }

    public void setFishBobber(FishBobber fishBobber) {
        this.fishBobber = fishBobber;
    }

    public void cancel() {
        if (getFishBobber() == null) {
            return;
        }
        setFishingState(FishingState.NONE);
        getFishBobber().getHook().remove();
        setFishBobber(null);
    }

    public void tick() {
        if (getFishingState() == FishingState.NONE) {
            cancel();
            return;
        }
        getFishBobber().tick();
        if (getFishingState() != Fisherman.FishingState.REELING) {
            return;
        }
        ItemStack fish = new ItemStack(Utilities.getFishItem(getFishBobber().getLured().getFish()));
        getPlayer().getInventory().addItem(fish);
        getFishBobber().getLured().getFish().remove();
        cancel();
    }

    enum FishingState {
        INITIAL,
        FISHING,
        LURING,
        HOOKED,
        REELING,
        NONE
    }
}
