package de.derredstoner.anticheat.check.impl.packet.badpacket;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInSteerVehicle;

@CheckInfo(
        name = "BadPacket (B)",
        description = "Ensures position packet order",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketB extends Check {

    public BadPacketB(PlayerData data) {
        super(data);
    }

    private int ticks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.connectionProcessor.totalTicks < 20 || data.movementProcessor.positionsSinceTeleport < 3) {
                ticks = 0;
                return;
            }

            if(wrapper.isMoving()) {
                if(ticks > 20) {
                    flag("ticks="+ticks);
                }

                ticks = 0;
            } else {
                ticks++;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInSteerVehicle) {
            ticks = 0;
        }
    }

}
