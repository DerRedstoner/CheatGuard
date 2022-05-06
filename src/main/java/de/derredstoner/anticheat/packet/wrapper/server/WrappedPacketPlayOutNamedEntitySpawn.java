package de.derredstoner.anticheat.packet.wrapper.server;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutNamedEntitySpawn extends WrappedPacket {

    private final int entityId;
    private final int posX;
    private final int posY;
    private final int posZ;

    public WrappedPacketPlayOutNamedEntitySpawn(PacketContainer packetContainer) {
        StructureModifier<Integer> integers = packetContainer.getIntegers();

        this.entityId = integers.read(0);
        this.posX = integers.read(1);
        this.posY = integers.read(2);
        this.posZ = integers.read(3);
    }
}
