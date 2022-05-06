package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedPacketPlayInSetCreativeSlot extends WrappedPacket {

    private final int slot;
    private final ItemStack itemStack;

    public WrappedPacketPlayInSetCreativeSlot(PacketContainer packetContainer) {
        this.slot = packetContainer.getIntegers().read(0);
        this.itemStack = packetContainer.getItemModifier().read(0);
    }

}
