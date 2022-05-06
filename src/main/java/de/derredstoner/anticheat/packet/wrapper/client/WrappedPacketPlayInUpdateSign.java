package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrappedPacketPlayInUpdateSign extends WrappedPacket {

    private final BlockPosition blockPosition;
    private final WrappedChatComponent[] chatComponents;

    public WrappedPacketPlayInUpdateSign(PacketContainer packetContainer) {
        this.blockPosition = packetContainer.getBlockPositionModifier().read(0);
        this.chatComponents = packetContainer.getChatComponentArrays().read(0);
    }
}
