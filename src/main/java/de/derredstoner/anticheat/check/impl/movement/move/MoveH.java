package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Move (H)",
        description = "Checks for high jump",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveH extends Check {

    public MoveH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.movementProcessor.teleporting
                    || data.player.getVehicle() != null
                    || data.movementProcessor.slimeTicks < 5
                    || data.player.isFlying()
                    || data.movementProcessor.collidingHorizontally
                    || data.player.getGameMode().equals(GameMode.SPECTATOR)
                    || data.player.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            }

            double deltaY = data.movementProcessor.deltaY;

            double expectedDeltaY = 0.42F + PlayerUtil.getAmplifier(data.player, PotionEffectType.JUMP) * 0.1F;

            if(data.velocityProcessor.predictedVelocityY > 0) {
                expectedDeltaY += data.velocityProcessor.predictedVelocityY;
            }

            if(!data.movementProcessor.clientGround && data.movementProcessor.lastClientGround && deltaY > expectedDeltaY + 1E-4) {
                flag("deltaY="+deltaY+"\nexpected="+expectedDeltaY);
            }
        }
    }

}
