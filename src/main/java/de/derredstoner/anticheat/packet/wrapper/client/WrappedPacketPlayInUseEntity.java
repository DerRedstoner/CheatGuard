package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import org.bukkit.util.Vector;

@Getter
public class WrappedPacketPlayInUseEntity extends WrappedPacket {

    private final int entityId;
    private final EnumWrappers.EntityUseAction action;
    private final Vector hitVec;

    public WrappedPacketPlayInUseEntity(PacketContainer packetContainer) {
        this.entityId = packetContainer.getIntegers().read(0);
        this.action = packetContainer.getEntityUseActions().read(0);
        this.hitVec = packetContainer.getVectors().read(0);
    }
}
