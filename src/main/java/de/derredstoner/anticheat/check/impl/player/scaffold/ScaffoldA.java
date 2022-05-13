package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Scaffold (A)",
        description = "Checks for post packet placement",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldA extends Check {

    public ScaffoldA(PlayerData data) {
        super(data);
    }

    private long lastFlying;
    private double average = 20.0;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            if(!data.player.getItemInHand().getType().isBlock()) return;

            double diff = time() - lastFlying;
            average = ((average * 9.0) + diff) / 10.0;

            if(average < 10 && !data.connectionProcessor.isLagging) {
                flag("average="+average);
                average = 20;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            lastFlying = time();
        }
    }

}
