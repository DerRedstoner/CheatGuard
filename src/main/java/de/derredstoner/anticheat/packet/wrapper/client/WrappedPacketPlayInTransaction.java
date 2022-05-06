package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInTransaction extends WrappedPacket {

    private final int windowId;
    private final short transactionId;
    private final boolean accepted;

    public WrappedPacketPlayInTransaction(PacketContainer packetContainer) {
        this.windowId = packetContainer.getIntegers().read(0);
        this.transactionId = packetContainer.getShorts().read(0);
        this.accepted = packetContainer.getBooleans().read(0);
    }
}
