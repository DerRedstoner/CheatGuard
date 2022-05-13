package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import org.bukkit.Material;

@CheckInfo(
        name = "Scaffold (I)",
        description = "Checks for safewalk",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldI extends Check {

    public ScaffoldI(PlayerData data) {
        super(data);
    }

    private long placedTicks;
    private double lastLastAccel, lastAccel;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(++placedTicks < 5 && isBridging() && !data.actionProcessor.sneaking && !data.player.getAllowFlight() && data.movementProcessor.positionsSinceTeleport > 2) {
                final double accel = data.movementProcessor.deltaXZ - data.movementProcessor.lastDeltaXZ;

                if(accel < 0.001 && lastAccel < 0.001 && lastLastAccel > 0.21) {
                    flag("1\naccel="+(float)accel+"\nlastAccel="+(float)lastAccel+"\nlastLastAccel="+(float)lastLastAccel);
                }

                if(accel > 0.01 && accel < 0.05 && lastAccel > 0.01 && lastAccel < 0.05 && lastLastAccel < -0.1) {
                    flag("2\naccel="+(float)accel+"\nlastAccel="+(float)lastAccel+"\nlastLastAccel="+(float)lastLastAccel);
                }

                lastLastAccel = lastAccel;
                lastAccel = accel;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            if(data.player.getItemInHand().getType().isBlock()) {
                placedTicks = 0;
            }
        }
    }

    public boolean isBridging() {
        return data.player.getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR;
    }

}
