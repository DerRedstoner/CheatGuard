package de.derredstoner.anticheat.check.impl.combat.killaura;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import org.bukkit.Bukkit;

@CheckInfo(
        name = "KillAura (D)",
        description = "Checks for blocking right before attack",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraD extends Check {

    public KillAuraD(PlayerData data) {
        super(data);
    }

    private boolean placed;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            final int face = wrapper.getFace();

            if(face == 255 && data.movementProcessor.deltaXZ > 0.2) {
                placed = true;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            if(placed) {
                if(buffer++ > 3) {
                    flag();
                }

                placed = false;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            placed = false;
        }
    }

}
