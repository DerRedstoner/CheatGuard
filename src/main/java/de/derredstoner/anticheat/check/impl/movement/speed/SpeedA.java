package de.derredstoner.anticheat.check.impl.movement.speed;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Speed (A)",
        description = "Checks for the client ignoring air friction",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class SpeedA extends Check {

    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.collidingHorizontally
                    || data.player.getAllowFlight()
                    || data.movementProcessor.teleportTicks < 40
                    || !data.movementProcessor.isChunkLoaded
                    || data.movementProcessor.getVelocityH() > 0
                    || data.player.getVehicle() != null) {
                buffer = 0;
                return;
            }

            final int clientAirTicks = data.movementProcessor.clientAirTicks;
            final int serverAirTicks = data.movementProcessor.serverAirTicks;

            final double deltaXZ = data.movementProcessor.deltaXZ;
            final double lastDeltaXZ = data.movementProcessor.lastDeltaXZ;

            final float prediction = (float) lastDeltaXZ * 0.91F + 0.026F;

            final float diff = (float) deltaXZ - prediction;

            if(clientAirTicks > 1 && serverAirTicks > 0 && diff > 0.001) {
                if(buffer++ > 1) {
                    flag("prediction="+prediction+"\ndeltaXZ="+deltaXZ+"\nlastDeltaXZ="+lastDeltaXZ+"\ndiff="+diff);
                    setback();
                }
            } else buffer = Math.max(0, buffer - 0.05);
        }
    }

}
