package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInClientCommand extends WrappedPacket {

    private final EnumWrappers.ClientCommand clientCommand;

    public WrappedPacketPlayInClientCommand(PacketContainer packetContainer) {
        this.clientCommand = packetContainer.getClientCommands().read(0);
    }
}
