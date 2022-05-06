package de.derredstoner.anticheat.check.impl.packet.badpacket;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInHeldItemSlot;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutHeldItemSlot;

@CheckInfo(
        name = "BadPacket (D)",
        description = "Checks for auto tool",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketD extends Check {

    public BadPacketD(PlayerData data) {
        super(data);
    }

    private int lastSlot;
    private boolean valid;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInHeldItemSlot) {
            WrappedPacketPlayInHeldItemSlot wrapper = (WrappedPacketPlayInHeldItemSlot) wrappedPacket;

            int currentSlot = wrapper.getSlot();

            if(valid && (currentSlot < 0 || currentSlot > 8 || currentSlot == lastSlot)) {
                flag("slot="+currentSlot+"\nlastSlot="+lastSlot);
            }

            lastSlot = currentSlot;
            valid = true;
        } else if(wrappedPacket instanceof WrappedPacketPlayOutHeldItemSlot) {
            valid = false;
        }
    }

}
