package uk.antiperson.betterfish;

import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    private final BetterFish betterFish;

    public Listeners(BetterFish betterFish) {
        this.betterFish = betterFish;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Fisherman fisherman = betterFish.getFishingManager().getFisherman(event.getPlayer());
        if (fisherman == null) {
            fisherman = new Fisherman(event.getPlayer(), event.getHook());
            betterFish.getFishingManager().addFisherman(fisherman);
        }
        if (fisherman.getFishBobber() == null) {
            fisherman.setFishBobber(new FishBobber(fisherman, event.getHook()));
        }
        switch (event.getState()) {
            case IN_GROUND:
            case CAUGHT_ENTITY:
                return;
            case BITE:
            case CAUGHT_FISH:
            case FAILED_ATTEMPT:
                throw new UnsupportedOperationException("Normal fishing is still occurring!");
            case FISHING:
                fisherman.setFishingState(Fisherman.FishingState.INITIAL);
                break;
            case REEL_IN:
                if (fisherman.getFishingState() == Fisherman.FishingState.HOOKED) {
                    ItemStack itemStack = new ItemStack(Utilities.getFishItem(fisherman.getFishBobber().getLured().getFish()));
                    event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
                    fisherman.setFishingState(Fisherman.FishingState.NONE);
                }
                fisherman.cancel();
                break;
        }
    }

    @EventHandler
    public void onHandSwitch(PlayerItemHeldEvent event) {
        ItemStack is = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if (is == null || is.getType() != Material.FISHING_ROD) {
            return;
        }
        Fisherman fisherman = betterFish.getFishingManager().getFisherman(event.getPlayer());
        if (fisherman == null) {
            return;
        }
        fisherman.cancel();
    }

    @EventHandler
    public void onFishHook(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        Fisherman fisherman = betterFish.getFishingManager().getFisherman(player);
        if (fisherman == null) {
            return;
        }
        fisherman.cancel();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Fisherman fisherman = betterFish.getFishingManager().getFisherman(event.getPlayer());
        if (fisherman == null) {
            return;
        }
        betterFish.getFishingManager().removeFisherman(fisherman);
    }
}
