package uk.antiperson.realfish;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class Listeners implements Listener {

    private final RealFish realFish;

    public Listeners(RealFish realFish) {
        this.realFish = realFish;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Fisherman fisherman = realFish.getFishingManager().getFisherman(event.getPlayer());
        if (fisherman == null) {
            fisherman = new Fisherman(event.getPlayer(), event.getHook());
            realFish.getFishingManager().addFisherman(fisherman);
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
                    ItemStack itemStack = new ItemStack(fisherman.getFishBobber().getLured().getItem());
                    event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
                    ExperienceOrb experienceOrb = (ExperienceOrb) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.EXPERIENCE_ORB);
                    experienceOrb.setExperience(ThreadLocalRandom.current().nextInt(1, 6));
                    ItemStack rod = fisherman.getRodItem();
                    Damageable damageable = (Damageable) rod.getItemMeta();
                    damageable.setDamage(damageable.getDamage() + 1);
                    rod.setItemMeta((ItemMeta) damageable);
                    event.getPlayer().getInventory().setItem(fisherman.getSlot(), rod);
                    fisherman.setFishingState(Fisherman.FishingState.NONE);
                    fisherman.getFishBobber().getLured().getFish().remove();
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
        Fisherman fisherman = realFish.getFishingManager().getFisherman(event.getPlayer());
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
        Fisherman fisherman = realFish.getFishingManager().getFisherman(player);
        if (fisherman == null) {
            return;
        }
        fisherman.cancel();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Fisherman fisherman = realFish.getFishingManager().getFisherman(event.getPlayer());
        if (fisherman == null) {
            return;
        }
        realFish.getFishingManager().removeFisherman(fisherman);
    }
}
