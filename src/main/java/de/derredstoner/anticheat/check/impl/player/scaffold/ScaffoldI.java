package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Scaffold (I)",
        description = "Checks for sprinting while bridging",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldI extends Check {

    public ScaffoldI(PlayerData data) {
        super(data);
    }

    private long placedTicks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(!data.movementProcessor.clientGround || data.movementProcessor.getVelocityH() > 0) {
                buffer = 0;
                return;
            }

            double deltaXZ = data.movementProcessor.deltaXZ;
            double maxDeltaXZ = 0.23 + (data.player.getWalkSpeed() - 0.2F) * 1.6F + PlayerUtil.getAmplifier(data.player, PotionEffectType.SPEED) * 0.048;

            if(wrapper.isMoving() && isBridging() && deltaXZ > maxDeltaXZ && placedTicks <= 1) {
                if(buffer++ > 2) {
                    flag("placedTicks="+placedTicks+"\ndeltaXZ="+deltaXZ+"\nmaxDeltaXZ="+maxDeltaXZ);
                }
            } else buffer = Math.max(0, buffer - 0.05);

            placedTicks++;
        } else if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(wrapper.getFace() != 255
                    && wrapper.getFacingY() != 0
                    && wrapper.getFacingY() != 1
                    && Math.abs(wrapper.getBlockPosition().getX() - data.movementProcessor.location.getX()) < 1.5
                    && wrapper.getBlockPosition().getY() < data.movementProcessor.location.getY()
                    && Math.abs(wrapper.getBlockPosition().getZ() - data.movementProcessor.location.getZ()) < 1.5) {
                placedTicks = 0;
            }
        }
    }

    public boolean isBridging() {
        return data.player.getLocation().clone().subtract(0, 2.5, 0).getBlock().getType() == Material.AIR;
    }

}
