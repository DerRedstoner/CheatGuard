package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInEntityAction extends WrappedPacket {

    private final int entityId;
    private final EnumWrappers.PlayerAction action;
    private final int auxId;

    public WrappedPacketPlayInEntityAction(PacketContainer packetContainer) {
        StructureModifier<Integer> integers = packetContainer.getIntegers();

        this.entityId = integers.read(0);
        this.action = packetContainer.getPlayerActions().read(0);
        this.auxId = integers.read(1);
    }
}
