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
        name = "BadPacket (F)",
        description = "Checks for look packets without rotation",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketF extends Check {

    public BadPacketF(PlayerData data) {
        super(data);
    }

    private boolean exempt;
    private float lastYaw, lastPitch;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isRotating() && !wrapper.isMoving()) {
                if(lastYaw == wrapper.getYaw() && lastPitch == wrapper.getPitch()) {
                    if(!exempt) {
                        flag();
                    }
                }

                lastYaw = wrapper.getYaw();
                lastPitch = wrapper.getPitch();
                exempt = false;
            } else exempt = true;
        } else if(wrappedPacket instanceof WrappedPacketPlayInSteerVehicle) {
            exempt = true;
        }
    }

}
