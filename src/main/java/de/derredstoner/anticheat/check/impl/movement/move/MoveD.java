package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;

@CheckInfo(
        name = "Move (D)",
        description = "Checks for invalid strafe in air",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveD extends Check {

    public MoveD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.teleportTicks < 20
                    || data.movementProcessor.getVelocityH() > 0
                    || data.actionProcessor.elytraFlying
                    || data.player.getVehicle() != null
                    || data.player.getAllowFlight()) {
                return;
            }

            double deltaX = data.movementProcessor.deltaX;
            double deltaZ = data.movementProcessor.deltaZ;
            double lastDeltaX = data.movementProcessor.lastDeltaX;
            double lastDeltaZ = data.movementProcessor.lastDeltaZ;

            boolean xChange = MathUtil.isOpposite(deltaX, lastDeltaX) && Math.abs(deltaX - lastDeltaX) > 0.23;
            boolean zChange = MathUtil.isOpposite(deltaZ, lastDeltaZ) && Math.abs(deltaZ - lastDeltaZ) > 0.23;

            if(!data.movementProcessor.clientGround
                    && Math.abs(data.movementProcessor.deltaY) < 0.42F
                    && data.movementProcessor.serverAirTicks > 0
                    && (xChange || zChange)) {
                flag("deltaX="+(float)deltaX+"\nlastDeltaX="+(float)lastDeltaX+"\ndeltaZ="+(float)deltaZ+"\nlastDeltaZ="+(float)lastDeltaZ);
                setback();
            }
        }
    }

}
