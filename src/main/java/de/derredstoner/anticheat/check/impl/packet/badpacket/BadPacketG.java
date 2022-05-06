package de.derredstoner.anticheat.check.impl.packet.badpacket;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockDig;

@CheckInfo(
        name = "BadPacket (G)",
        description = "Checks for invalid release use item packet",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketG extends Check {

    public BadPacketG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockDig) {
            WrappedPacketPlayInBlockDig wrapper = (WrappedPacketPlayInBlockDig) wrappedPacket;

            if(wrapper.getPlayerDigType() == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM && (wrapper.getBlockPosition().getX() != 0 || wrapper.getBlockPosition().getY() != 0 || wrapper.getBlockPosition().getZ() != 0)) {
                flag();
            }
        }
    }

}
