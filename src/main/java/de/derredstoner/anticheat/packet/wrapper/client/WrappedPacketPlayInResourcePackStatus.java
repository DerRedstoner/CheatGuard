package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInResourcePackStatus extends WrappedPacket {

    private final EnumWrappers.ResourcePackStatus resourcePackStatus;

    public WrappedPacketPlayInResourcePackStatus(PacketContainer packetContainer) {
        this.resourcePackStatus = packetContainer.getResourcePackStatus().read(0);
    }
}
