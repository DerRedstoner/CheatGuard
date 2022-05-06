package de.derredstoner.anticheat.check.impl.packet.nofall;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "NoFall (B)",
        description = "Checks for multiple spoofed ground states",
        category = Category.PACKET,
        subCategory = SubCategory.NOFALL
)
public class NoFallB extends Check {

    public NoFallB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.flightTicks < 10
                    || data.connectionProcessor.totalTicks - data.actionProcessor.lastBlockPlace < 3) {
                return;
            }

            boolean mathGround = data.movementProcessor.location.getY() % (1/64D) == 0;

            if(data.movementProcessor.serverAirTicks == 0) {
                buffer = 0;
            }

            if(mathGround && data.movementProcessor.clientGround && data.movementProcessor.serverAirTicks > 10) {
                if(buffer++ > 0) {
                    flag();
                }
            }
        }
    }

}
