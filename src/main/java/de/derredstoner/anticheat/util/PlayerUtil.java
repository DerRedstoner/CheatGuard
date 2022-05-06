package de.derredstoner.anticheat.util;

import de.derredstoner.anticheat.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerUtil {

    public static Block getBlockAsync(Location loc ) throws NullPointerException {
        if (loc == null) throw new NullPointerException("null location");
        if (Objects.requireNonNull(loc.getWorld()).isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
            return loc.getBlock();
        return null;
    }

    public static float getFriction(PlayerData data) {
        String block = data.player.getLocation().add(0, -1, 0).getBlock().getType().name().toLowerCase();
        return block.equals("blue ice") ? 0.989f : block.contains("ice") ? 0.98f : block.equals("slime") ? 0.8f : 0.6f;
    }

    public static boolean isChunkLoaded(Location location) {
        return (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4));
    }

    public static int getAmplifier(Player player, PotionEffectType effectType) {
        if(!player.hasPotionEffect(effectType)) {
            return 0;
        }
        for(PotionEffect effect : player.getActivePotionEffects()) {
            if(effect.getType().getName().equals(effectType.getName())) {
                return effect.getAmplifier() + 1;
            }
        }
        return 0;
    }

}
