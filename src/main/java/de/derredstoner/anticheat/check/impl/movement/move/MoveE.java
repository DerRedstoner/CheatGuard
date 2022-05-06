package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Move (E)",
        description = "Checks for jumping mid air",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveE extends Check {

    public MoveE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.positionsSinceTeleport < 3) {
                return;
            }

            if(data.movementProcessor.deltaY == 0.42F
                    && !data.movementProcessor.clientGround
                    && !data.movementProcessor.lastClientGround
                    && !data.movementProcessor.collidingHorizontally
                    && data.movementProcessor.serverAirTicks > 1) {
                flag();
                setback();
            }
        }
    }

}
