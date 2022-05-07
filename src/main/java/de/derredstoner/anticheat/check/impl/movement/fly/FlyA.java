package de.derredstoner.anticheat.check.impl.movement.fly;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Fly (A)",
        description = "Checks for positive acceleration mid air",
        category = Category.MOVEMENT,
        subCategory = SubCategory.FLY
)
public class FlyA extends Check {

    public FlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.movementProcessor.webTicks < 5
                    || data.movementProcessor.touchingLiquid
                    || !data.movementProcessor.isChunkLoaded
                    || data.player.getAllowFlight()
                    || data.actionProcessor.elytraFlying
                    || data.movementProcessor.getVelocityV() > 0
                    || data.movementProcessor.positionsSinceTeleport < 3) {
                return;
            }

            if(wrapper.isMoving()) {
                final double deltaY = data.movementProcessor.deltaY;
                final double lastDeltaY = data.movementProcessor.lastDeltaY;

                if(!data.movementProcessor.clientGround && !data.movementProcessor.lastClientGround && data.movementProcessor.serverAirTicks > 1) {
                    final double offset = (lastDeltaY - 0.08) * 0.9800000190734863;

                    if(Math.abs(offset - deltaY) > 0.001) {
                        if(buffer++ > 1) {
                            flag("deltaY="+deltaY+"\nlastDeltaY="+lastDeltaY+"\noffset="+offset);
                            setback();
                        }
                    } else buffer = Math.max(0, buffer - 0.01);
                } else buffer = 0;
            }
        }
    }

}
