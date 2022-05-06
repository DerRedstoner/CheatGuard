package de.derredstoner.anticheat.check.impl.packet.badpacket;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInEntityAction;

@CheckInfo(
        name = "BadPacket (H)",
        description = "Checks for sprint flaw",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketH extends Check {

    public BadPacketH(PlayerData data) {
        super(data);
    }

    private long lastTimestamp;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SPRINTING) {
                long delta = time() - lastTimestamp;

                if(delta < 75L) {
                    if(time() - data.actionProcessor.lastHit < 1000L) {
                        if(buffer++ > 15) {
                            flag("delta="+delta);
                        }
                    }
                } else buffer = 0;
            } else if(wrapper.getAction() == EnumWrappers.PlayerAction.STOP_SPRINTING) {
                lastTimestamp = time();
            }
        }
    }

}
