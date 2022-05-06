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

@CheckInfo(
        name = "KillAura (C)",
        description = "Checks for blocking right after attack",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraC extends Check {

    public KillAuraC(PlayerData data) {
        super(data);
    }

    private boolean sent;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            final int face = wrapper.getFace();

            if(face != 255 && sent) {
                flag();
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            sent = true;
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            sent = false;
        }
    }

}
