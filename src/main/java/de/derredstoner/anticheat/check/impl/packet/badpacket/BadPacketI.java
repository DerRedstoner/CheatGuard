package de.derredstoner.anticheat.check.impl.packet.badpacket;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInSteerVehicle;

@CheckInfo(
        name = "BadPacket (I)",
        description = "Checks for bad steer vehicle packet values",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketI extends Check {

    public BadPacketI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInSteerVehicle) {
            WrappedPacketPlayInSteerVehicle wrapper = (WrappedPacketPlayInSteerVehicle) wrappedPacket;

            float forward = wrapper.getForwardSpeed();
            float sideways = wrapper.getStrafeSpeed();

            if(Math.abs(forward) > 0.98F || Math.abs(sideways) > 0.98F) {
                flag("forward="+forward+"\nsideways="+sideways);
            }
        }
    }

}
