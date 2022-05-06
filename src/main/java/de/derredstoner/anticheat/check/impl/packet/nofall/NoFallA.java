package de.derredstoner.anticheat.check.impl.packet.nofall;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "NoFall (A)",
        description = "Checks for spoofed ground state",
        category = Category.PACKET,
        subCategory = SubCategory.NOFALL
)
public class NoFallA extends Check {

    public NoFallA(PlayerData data) {
        super(data);
    }

    private boolean lastMathGround;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.flightTicks < 20 || data.movementProcessor.teleportTicks < 40) {
                return;
            }

            boolean mathGround = data.movementProcessor.location.getY() % (1/64D) == 0;

            if(data.movementProcessor.clientGround && !mathGround && !lastMathGround && data.movementProcessor.serverAirTicks > 8) {
                flag("clientGround");
            }

            if(mathGround && !data.movementProcessor.clientGround && data.movementProcessor.serverAirTicks > 1) {
                flag("mathGround");
            }

            lastMathGround = mathGround;
        }
    }

}
