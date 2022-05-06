package de.derredstoner.anticheat.check.impl.combat.killaura;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import org.bukkit.GameMode;

@CheckInfo(
        name = "KillAura (B)",
        description = "Checks for zero pitch rotations",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraB extends Check {

    public KillAuraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.player.getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }

            if(wrapper.isRotating()) {
                if(time() - data.actionProcessor.lastHit < 500L) {
                    final float pitch = data.movementProcessor.pitch;

                    if(pitch == 0 && !data.movementProcessor.teleporting) {
                        if(buffer++ > 0) {
                            flag();
                        }
                    } else buffer = 0;
                }
            }
        }
    }

}
