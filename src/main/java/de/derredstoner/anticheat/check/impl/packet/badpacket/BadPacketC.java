package de.derredstoner.anticheat.check.impl.packet.badpacket;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;

@CheckInfo(
        name = "BadPacket (C)",
        description = "Checks for invalid entity id",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketC extends Check {

    public BadPacketC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            if(wrapper.getEntityId() < 0 || wrapper.getEntityId() == data.player.getEntityId()) {
                flag("entityId="+wrapper.getEntityId());
            }
        }
    }

}
