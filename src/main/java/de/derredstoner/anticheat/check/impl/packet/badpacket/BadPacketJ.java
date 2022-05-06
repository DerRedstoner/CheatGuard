package de.derredstoner.anticheat.check.impl.packet.badpacket;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;

@CheckInfo(
        name = "BadPacket (J)",
        description = "Checks for small GCD in y coordinates",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketJ extends Check {

    public BadPacketJ(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.positionsSinceTeleport < 3) {
                return;
            }

            double gcd = MathUtil.gcd(data.movementProcessor.location.getY(), data.movementProcessor.lastLocation.getY());

            if(String.valueOf(gcd).contains("E")) {
                flag("gcd="+gcd);
            }
        }
    }

}
