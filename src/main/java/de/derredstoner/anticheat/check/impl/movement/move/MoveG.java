package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Move (G)",
        description = "Checks for repeated vertical speed changes",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveG extends Check {

    public MoveG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.collidingVerticallyUp
                    || data.movementProcessor.slimeTicks < 10
                    || data.movementProcessor.teleporting
                    || data.player.getVehicle() != null
                    || data.movementProcessor.getVelocityH() > 0) {
                buffer = 0;
                return;
            }

            float deltaXZ = (float) data.movementProcessor.deltaXZ;
            float lastDeltaXZ = (float) data.movementProcessor.lastDeltaXZ;
            float deltaY = (float) data.movementProcessor.deltaY;
            float lastDeltaY = (float) data.movementProcessor.lastDeltaY;

            if(deltaXZ > 0.15 && lastDeltaXZ > 0.15 && ((deltaY > 0 && lastDeltaY < 0) || (deltaY < 0 && lastDeltaY > 0))) {
                if(buffer++ > 3) {
                    flag("deltaY="+deltaY+"\nlastDeltaY="+lastDeltaY);
                    setback();
                }
            } else buffer = Math.max(0, buffer - 0.2);
        }
    }

}
