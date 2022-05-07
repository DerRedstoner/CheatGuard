package de.derredstoner.anticheat.check.impl.movement.jesus;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import org.bukkit.GameMode;

@CheckInfo(
        name = "Jesus (A)",
        description = "Checks for weird movement in water",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class JesusA extends Check {

    public JesusA(PlayerData data) {
        super(data);
    }

    private double buffer2;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.player.getVehicle() != null
                    || data.movementProcessor.teleporting
                    || data.actionProcessor.elytraFlying
                    || data.player.getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }

            if(data.movementProcessor.touchingLiquid && data.movementProcessor.clientAirTicks > 3) {
                double vel = data.player.getVelocity().getY() + 0.0784000015258789;

                if(vel > 0) {
                    return;
                }

                double deltaY = data.movementProcessor.deltaY;
                double lastDeltaY = data.movementProcessor.lastDeltaY;
                double diff = Math.abs(vel - deltaY);

                if(diff > 0.8) {
                    if(buffer++ > 3) {
                        flag("difference="+diff);
                    }
                } else buffer = Math.max(0, buffer - 0.05);

                if(!data.movementProcessor.collidingVerticallyUp
                        && data.movementProcessor.deltaXZ > 0.1
                        && Math.abs(deltaY) < 0.01
                        && Math.abs(deltaY - lastDeltaY) < 0.01) {
                    if(buffer2++ > 1) {
                        flag("deltaY="+deltaY);
                        buffer2--;
                    }
                } else buffer2 = Math.max(0, buffer2 - 0.1);
            } else buffer = 0;
        }
    }

}
