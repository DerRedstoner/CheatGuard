package de.derredstoner.anticheat.packet.wrapper.server;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutEntityVelocity extends WrappedPacket {

    private final int entityId;

    private final int velocityX;
    private final int velocityY;
    private final int velocityZ;

    public WrappedPacketPlayOutEntityVelocity(PacketContainer packetContainer) {
        StructureModifier<Integer> integers = packetContainer.getIntegers();

        this.entityId = integers.read(0);
        this.velocityX = integers.read(1);
        this.velocityY = integers.read(2);
        this.velocityZ = integers.read(3);
    }
}
