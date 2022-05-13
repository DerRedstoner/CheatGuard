package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInHeldItemSlot;

@CheckInfo(
        name = "Scaffold (H)",
        description = "Checks for fast item switch speed",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldH extends Check {

    public ScaffoldH(PlayerData data) {
        super(data);
    }

    private boolean placed;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            placed = false;
        } else if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace packet = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(packet.getFacingY() == 0.0 || !packet.getItemStack().getType().isBlock()) return;

            placed = true;
        } else if(wrappedPacket instanceof WrappedPacketPlayInHeldItemSlot) {
            if(placed && data.movementProcessor.deltaXZ > 0.2) {
                flag();
            }

            placed = false;
        }
    }

}
