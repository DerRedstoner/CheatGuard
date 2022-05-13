package de.derredstoner.anticheat.check.impl.player.inventory;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInWindowClick;

@CheckInfo(
        name = "Inventory (A)",
        description = "Checks for inventory clicks while sprinting",
        category = Category.PLAYER,
        subCategory = SubCategory.INVENTORY
)
public class InventoryA extends Check {

    public InventoryA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInWindowClick) {
            if(data.player.getVehicle() != null) {
                return;
            }

            if(!data.actionProcessor.inventoryOpen && data.actionProcessor.sprinting) {
                if(buffer++ > 0) {
                    flag();
                }
            } else buffer = 0;
        }
    }

}
