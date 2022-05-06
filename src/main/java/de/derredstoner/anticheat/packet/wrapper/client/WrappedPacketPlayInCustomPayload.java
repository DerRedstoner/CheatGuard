package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInCustomPayload extends WrappedPacket {

    private final String payload;

    public WrappedPacketPlayInCustomPayload(PacketContainer packetContainer) {
        this.payload = packetContainer.getStrings().read(0);
    }
}
