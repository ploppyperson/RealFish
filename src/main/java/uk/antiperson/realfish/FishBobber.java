package uk.antiperson.realfish;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class FishBobber {

    private final Fisherman owner;
    private final FishHook hook;
    private LuredFish lured;
    private int wait;
    private int bobbingWait;
    private int bobbingTotal;
    private boolean bobbleDown;
    private Block block;
    private ItemStack itemStack;

    public FishBobber(Fisherman owner, FishHook hook) {
        this.owner = owner;
        this.hook = hook;
    }

    private int calculateWait() {
        int randTicks = ThreadLocalRandom.current().nextInt(100, 600);
        // getItemInUse no work?
        int lureLevel = getOwner().getRodItem().getEnchantmentLevel(Enchantment.LURE);
        return randTicks - (lureLevel * 100);
    }

    public Block getBlock() {
        if (block == null) {
            block = getHook().getLocation().getBlock();
        }
        return block;
    }

    public int calculateDecrement() {
        int skyLight = getBlock().getLightFromSky();
        if (skyLight < 15 && ThreadLocalRandom.current().nextBoolean()) {
            return 0;
        }
        if (skyLight == 15 && getHook().getWorld().hasStorm() && ThreadLocalRandom.current().nextDouble() <= 0.25) {
            return 2;
        }
        return 1;
    }

    public int getWait() {
        return wait;
    }

    public FishHook getHook() {
        return hook;
    }

    public LuredFish getLured() {
        return lured;
    }

    public Fisherman getOwner() {
        return owner;
    }

    public void setWait(int wait) {
        if (wait < 0) {
            throw new IllegalArgumentException("New wait is less than 0.");
        }
        this.wait = wait;
    }

    public void tick() {
        if (getOwner().getFishingState() == Fisherman.FishingState.MISSED) {
            return;
        }
        if (getOwner().getFishingState() == Fisherman.FishingState.INITIAL) {
            if (getHook().getState() != FishHook.HookState.BOBBING) {
                return;
            }
            int calculatedWait = calculateWait();
            if (calculatedWait <= 0) {
                return;
            }
            setWait(calculatedWait);
            getOwner().setFishingState(Fisherman.FishingState.FISHING);
            hook.setMaxWaitTime(Integer.MAX_VALUE);
            hook.setMinWaitTime(Integer.MAX_VALUE);
            return;
        }
        if (getOwner().getFishingState() == Fisherman.FishingState.FISHING) {
            int newWait = Math.max(getWait() - calculateDecrement(), 0);
            if (newWait == 0) {
                getOwner().setFishingState(Fisherman.FishingState.LURING);
            }
            setWait(newWait);
            return;
        }
        if (getOwner().getFishingState() == Fisherman.FishingState.LURING) {
            if (getLured() == null) {
                lured = findNearbyFish();
                if (getLured() == null) {
                    getOwner().setFishingState(Fisherman.FishingState.INITIAL);
                }
                return;
            }
            getLured().tick();
        }
        if (getOwner().getFishingState() == Fisherman.FishingState.HOOKED) {
            getLured().tick();
            if (bobbingWait > 0) {
                bobbingWait -= 1;
                return;
            }
            if (bobbingTotal == 0) {
                bobbingTotal = ThreadLocalRandom.current().nextInt(20,100);
                getLured().getFish().addPassenger(getHook());
            }
            if (bobbingWait == 0) {
                bobbingWait = ThreadLocalRandom.current().nextInt(10,15);
            }
            bobbingTotal -= bobbingWait;
            if (bobbingTotal <= 0) {
                getOwner().setFishingState(Fisherman.FishingState.MISSED);
                getLured().getFish().removePassenger(getHook());
                return;
            }
            double y = bobbleDown ? ThreadLocalRandom.current().nextDouble(0.0, 0.1) : ThreadLocalRandom.current().nextDouble(-0.1, 0);
            Vector bobble = new Vector(0, y,0);
            getLured().getFish().setVelocity(bobble);
            bobbleDown = !bobbleDown;
        }
    }

    private LuredFish findNearbyFish() {
        for (Entity entity : getHook().getNearbyEntities(4,10,4)) {
            if (entity instanceof Fish) {
                return new LuredFish(this, (Fish) entity, 0.05);
            }
        }
        return null;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
