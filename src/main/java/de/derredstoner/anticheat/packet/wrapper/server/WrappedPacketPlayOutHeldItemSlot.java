package de.derredstoner.anticheat.packet.wrapper.server;

import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutHeldItemSlot extends WrappedPacket {

    private final int slot;

    public WrappedPacketPlayOutHeldItemSlot(PacketContainer packetContainer) {
        this.slot = packetContainer.getIntegers().read(0);
    }

}
