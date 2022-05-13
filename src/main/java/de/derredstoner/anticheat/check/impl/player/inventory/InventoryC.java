package de.derredstoner.anticheat.check.impl.player.inventory;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;

@CheckInfo(
        name = "Inventory (C)",
        description = "Checks for head movements while in inventory",
        category = Category.PLAYER,
        subCategory = SubCategory.INVENTORY
)
public class InventoryC extends Check {

    public InventoryC(PlayerData data) {
        super(data);
    }

    private int ticks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.player.getVehicle() != null || data.movementProcessor.teleporting) {
                return;
            }

            if(data.actionProcessor.inventoryOpen) {
                ticks++;
            } else ticks = 0;

            if(wrapper.isRotating()) {
                final boolean headMovement = data.movementProcessor.deltaPitch != 0 || data.movementProcessor.deltaYaw != 0;

                if(ticks > data.connectionProcessor.transactionPing / 50 + 5 && headMovement) {
                    flag("deltaYaw="+data.movementProcessor.deltaYaw+"\ndeltaPitch="+data.movementProcessor.deltaPitch);
                }
            }
        }
    }

}
