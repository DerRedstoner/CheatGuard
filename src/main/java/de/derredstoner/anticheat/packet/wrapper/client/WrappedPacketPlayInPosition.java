package de.derredstoner.anticheat.packet.wrapper.client;

import com.comphenix.protocol.events.PacketContainer;

public class WrappedPacketPlayInPosition extends WrappedPacketPlayInFlying {

    public WrappedPacketPlayInPosition(PacketContainer packetContainer) {
        super(packetContainer);
    }
}
