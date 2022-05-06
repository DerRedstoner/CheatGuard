package de.derredstoner.anticheat.packet.wrapper.client;

import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInHeldItemSlot extends WrappedPacket {

    private final int slot;

    public WrappedPacketPlayInHeldItemSlot(PacketContainer packetContainer) {
        this.slot = packetContainer.getIntegers().read(0);
    }

}
