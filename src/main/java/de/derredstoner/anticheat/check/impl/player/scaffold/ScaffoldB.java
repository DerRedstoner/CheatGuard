package de.derredstoner.anticheat.check.impl.player.scaffold;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInEntityAction;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Scaffold (B)",
        description = "Checks for invalid sneak timing",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldB extends Check {

    public ScaffoldB(PlayerData data) {
        super(data);
    }

    private long lastFlying, lastPlace;
    private double average = 20.0;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(wrapper.getBlockPosition() == null || wrapper.getBlockPosition().getY() == -1) {
                return;
            }

            if(!data.player.getItemInHand().getType().isBlock()) return;

            lastPlace = time();
        } else if(wrappedPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SNEAKING && time() - lastPlace < 300L) {
                double diff = time() - lastFlying;
                average = ((average * 4.0) + diff) / 5.0;

                if(average < 10 && !data.connectionProcessor.isLagging) {
                    flag("average="+average);
                    average = 20;
                }
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            lastFlying = time();
        }
    }

}
