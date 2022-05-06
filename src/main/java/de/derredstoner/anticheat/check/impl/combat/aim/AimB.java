package de.derredstoner.anticheat.check.impl.combat.aim;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import org.bukkit.GameMode;

@CheckInfo(
        name = "Aim (B)",
        description = "Checks for rounded pitch values",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM
)
public class AimB extends Check {

    public AimB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.movementProcessor.teleporting
                    || data.connectionProcessor.totalTicks < 20
                    || data.player.getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }

            if(wrapper.isRotating() && time() - data.actionProcessor.lastHit < 250L) {
                final float deltaPitch = data.movementProcessor.deltaPitch;

                if(deltaPitch != 0 && (deltaPitch % 0.1 == 0 || deltaPitch % 0.25 == 0)) {
                    if(buffer++ > 5) {
                        flag("deltaPitch="+deltaPitch);
                    }
                } else buffer = Math.max(0, buffer - 0.25);
            }
        }
    }

}
