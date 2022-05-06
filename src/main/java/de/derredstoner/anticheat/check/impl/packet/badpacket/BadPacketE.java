package de.derredstoner.anticheat.check.impl.packet.badpacket;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInEntityAction;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;

@CheckInfo(
        name = "BadPacket (E)",
        description = "Checks for more knockback",
        category = Category.PACKET,
        subCategory = SubCategory.BADPACKET
)
public class BadPacketE extends Check {

    public BadPacketE(PlayerData data) {
        super(data);
    }

    private boolean sent;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            sent = false;
        } else if(wrappedPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SPRINTING || wrapper.getAction() == EnumWrappers.PlayerAction.STOP_SPRINTING) {
                sent = true;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                if(sent && data.movementProcessor.deltaXZ > 0) {
                    if(buffer++ > 0) {
                        flag();
                    }
                } else buffer = 0;

                sent = false;
            }
        }
    }

}
