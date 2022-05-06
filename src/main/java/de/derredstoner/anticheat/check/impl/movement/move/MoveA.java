package de.derredstoner.anticheat.check.impl.movement.move;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import org.bukkit.GameMode;

@CheckInfo(
        name = "Move (A)",
        description = "Checks for invalid fall speed",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class MoveA extends Check {

    public MoveA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(data.player.isInsideVehicle()
                    || data.movementProcessor.teleporting
                    || data.player.getGameMode().equals(GameMode.CREATIVE)
                    || data.player.getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }

            float deltaY = (float) data.movementProcessor.deltaY;

            if(deltaY < -3.92 || deltaY > 256) {
                flag("deltaY="+deltaY);
                setback();
            }
        }
    }

}
