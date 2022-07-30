package de.derredstoner.anticheat.check.impl.combat.velocity;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Velocity (A)",
        description = "Checks for vertical velocity modifications",
        category = Category.COMBAT,
        subCategory = SubCategory.VELOCITY
)
public class VelocityA extends Check {

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.flightTicks < 10
                    || data.movementProcessor.teleporting
                    || data.player.getVehicle() != null
                    || data.movementProcessor.collidingHorizontally
                    || data.movementProcessor.collidingVerticallyUp) {
                return;
            }

            final int tick = data.velocityProcessor.velocityTicks;

            final double deltaY = data.movementProcessor.deltaY;
            final double predictedVelocity = data.velocityProcessor.predictedVelocityY;

            final double percentage = deltaY / predictedVelocity;

            if(tick < 3 && predictedVelocity > 0) {
                if(percentage >= 0 && percentage < 0.9999) {
                    if(buffer++ > 1) {
                        flag("tick="+tick+"\npercentage="+(percentage*100F)+"\ndeltaY="+deltaY+"\npredicted="+predictedVelocity);
                    }
                } else buffer = Math.max(0, buffer - 0.1);
            }
        }
    }

}
