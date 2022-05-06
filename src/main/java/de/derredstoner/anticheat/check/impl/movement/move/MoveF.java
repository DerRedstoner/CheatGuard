package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Move (F)",
        description = "Checks for low jumps",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveF extends Check {

    public MoveF(PlayerData data) {
        super(data);
    }

    private int bypassTicks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            bypassTicks = Math.max(0, bypassTicks - 1);

            if(bypassTicks > 0
                    || data.movementProcessor.teleporting
                    || data.movementProcessor.slimeTicks < 10
                    || data.movementProcessor.touchingLiquid
                    || data.movementProcessor.getVelocityV() > 0) {
                return;
            }

            if(data.movementProcessor.collidingVerticallyUp) {
                bypassTicks = 20;
                return;
            }

            float deltaY = (float) data.movementProcessor.deltaY;

            boolean mathGround = data.movementProcessor.location.getY() % (1/64D) == 0;

            if(!data.movementProcessor.clientGround && !mathGround && data.movementProcessor.lastClientGround) {
                if(deltaY > 0 && deltaY < 0.404444F) {
                    if(buffer++ > 1) {
                        flag("deltaY="+deltaY);
                        setback();
                    }
                } else buffer = Math.max(0, buffer - 0.1);
            }
        }
    }

}
