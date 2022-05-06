package de.derredstoner.anticheat.check.impl.movement.fly;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Fly (B)",
        description = "Ensures that the client applies normal gravity",
        category = Category.MOVEMENT,
        subCategory = SubCategory.FLY
)
public class FlyB extends Check {

    public FlyB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying event = (WrappedPacketPlayInFlying) wrappedPacket;

            if(event.isMoving()) {
                if(data.movementProcessor.positionsSinceTeleport < 3
                        || data.movementProcessor.getVelocityV() > 0
                        || data.movementProcessor.flightTicks < 10
                        || data.movementProcessor.collidingHorizontally
                        || data.connectionProcessor.totalTicks - data.actionProcessor.lastBlockPlace < 3
                        || !data.movementProcessor.isChunkLoaded) {
                    return;
                }

                if(data.movementProcessor.serverAirTicks > 1
                        && data.movementProcessor.clientAirTicks > 1
                        && data.movementProcessor.deltaY >= data.movementProcessor.lastDeltaY
                        && data.movementProcessor.deltaY != 0.42F) {
                    if(buffer++ > 1) {
                        flag("deltaY="+data.movementProcessor.deltaY+"\nlastDeltaY="+data.movementProcessor.lastDeltaY);
                        setback();
                    }
                } else buffer = Math.max(0, buffer - 0.01);
            }
        }
    }

}
