package de.derredstoner.anticheat.packet.wrapper.client;

import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInTabComplete extends WrappedPacket {

    private final String message;

    public WrappedPacketPlayInTabComplete(PacketContainer packetContainer) {
        this.message = packetContainer.getStrings().read(0);
    }
}