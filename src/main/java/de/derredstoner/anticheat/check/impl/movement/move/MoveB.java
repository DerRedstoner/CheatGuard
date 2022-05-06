package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Move (B)",
        description = "Checks for invalid climb speed on ladders",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveB extends Check {

    public MoveB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.player.getAllowFlight() || data.movementProcessor.getVelocityV() > 0) {
                return;
            }

            if(data.movementProcessor.touchingClimbable
                    && !data.movementProcessor.clientGround
                    && (float) data.movementProcessor.deltaY > 0.1177F
                    && data.movementProcessor.deltaY >= data.movementProcessor.lastDeltaY) {
                if(buffer++ > 1) {
                    flag("deltaY="+(float)data.movementProcessor.deltaY);
                    setback();
                }
            } else {
                buffer = 0;
            }
        }
    }

}
