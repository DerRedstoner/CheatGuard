package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInCloseWindow extends WrappedPacket {

    private final int windowId;

    public WrappedPacketPlayInCloseWindow(PacketContainer packetContainer) {
        this.windowId = packetContainer.getIntegers().read(0);
    }
}
