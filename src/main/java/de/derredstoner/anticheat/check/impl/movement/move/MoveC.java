package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Move (C)",
        description = "Checks for stepping up full blocks",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveC extends Check {

    public MoveC(PlayerData data) {
        super(data);
    }

    private boolean lastServerGround;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isMoving()) {
                if(data.movementProcessor.getVelocityV() > 0
                        || data.actionProcessor.elytraFlying
                        || data.movementProcessor.teleporting) {
                    return;
                }

                float deltaY = (float) data.movementProcessor.deltaY;

                boolean serverGround = data.movementProcessor.location.getY() % (1/64D) == 0;

                if(serverGround && lastServerGround && deltaY > 0.6F) {
                    flag("deltaY="+deltaY);
                    setback();
                }

                lastServerGround = serverGround;
            }
        }
    }

}
