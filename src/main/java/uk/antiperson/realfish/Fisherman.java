package uk.antiperson.realfish;

import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Fisherman {

    private final Player player;
    private FishBobber fishBobber;
    private FishingState fishingState;

    public Fisherman(Player player, FishHook fishHook) {
        this.player = player;
        this.fishBobber = new FishBobber(this, fishHook);
    }

    public EquipmentSlot getSlot() {
        EquipmentSlot[] hands = new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
        for (EquipmentSlot hand : hands) {
            ItemStack itemStack = player.getInventory().getItem(hand);
            if (itemStack != null && itemStack.getType() == Material.FISHING_ROD) {
                return hand;
            }
        }
        return null;
    }

    public ItemStack getRodItem() {
        EquipmentSlot slot = getSlot();
        return slot == null ? null : getPlayer().getInventory().getItem(slot);
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
        //System.out.println(getFishingState() + "," + getFishBobber().getWait());
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
        cancel();
    }

    enum FishingState {
        INITIAL,
        FISHING,
        LURING,
        HOOKED,
        REELING,
        MISSED,
        NONE
    }
}
