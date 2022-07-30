package de.derredstoner.anticheat.check.impl.combat.velocity;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.WorldBorder;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Velocity (B)",
        description = "Checks for horizontal velocity modifications",
        category = Category.COMBAT,
        subCategory = SubCategory.VELOCITY,
        experimental = true
)
public class VelocityB extends Check {

    public VelocityB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.flightTicks < 10
                    || data.movementProcessor.teleportTicks < 40
                    || data.player.getVehicle() != null
                    || data.movementProcessor.nearWall
                    || PlayerUtil.getAmplifier(data.player, PotionEffectType.SPEED) > 3
                    || data.movementProcessor.collidingHorizontally
                    || data.movementProcessor.collidingVerticallyUp) {
                return;
            }

            final int tick = data.velocityProcessor.velocityTicks;

            final WorldBorder worldBorder = data.player.getWorld().getWorldBorder();
            if(Math.abs(data.movementProcessor.location.getX() - worldBorder.getCenter().getX()) >= worldBorder.getSize()/2-2
                    || Math.abs(data.movementProcessor.location.getZ() - worldBorder.getCenter().getZ()) >= worldBorder.getSize()/2-2) {
                return;
            }

            final double deltaXZ = data.movementProcessor.deltaXZ;
            final double predictedVelocity = data.velocityProcessor.predictedVelocityH;

            final double percentage = deltaXZ / predictedVelocity;

            if(tick == 1) {
                if(percentage >= 0 && percentage < 0.999) {
                    if(buffer++ > 1) {
                        flag("tick="+tick+"\npercentage="+(percentage*100.0F)+"\ndeltaXZ="+deltaXZ+"\npredicted="+predictedVelocity);
                    }
                } else buffer = Math.max(0, buffer - 0.05);
            }
        }
    }

}
