package de.derredstoner.anticheat.check.impl.player.inventory;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInWindowClick;

@CheckInfo(
        name = "Inventory (B)",
        description = "Checks for inventory clicks while walking",
        category = Category.PLAYER,
        subCategory = SubCategory.INVENTORY
)
public class InventoryB extends Check {

    public InventoryB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInWindowClick) {
            if(data.movementProcessor.getVelocityH() > 0
                    || data.movementProcessor.touchingLiquid
                    || data.player.getVehicle() != null) {
                return;
            }

            double accel = data.movementProcessor.deltaXZ - data.movementProcessor.lastDeltaXZ;

            if(data.movementProcessor.deltaXZ > 0.21 && accel >= 0) {
                if(buffer++ > 0) {
                    flag("deltaXZ="+(float)data.movementProcessor.deltaXZ+"\nacceleration="+(float)accel);
                }
            } else buffer = 0;
        }
    }

}
