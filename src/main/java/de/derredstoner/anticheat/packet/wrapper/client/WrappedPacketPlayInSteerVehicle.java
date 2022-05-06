package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInSteerVehicle extends WrappedPacket {

    private final float strafeSpeed;
    private final float forwardSpeed;
    private final boolean jumping;
    private final boolean sneaking;

    public WrappedPacketPlayInSteerVehicle(PacketContainer packetContainer) {
        StructureModifier<Float> floats = packetContainer.getFloat();
        StructureModifier<Boolean> booleans = packetContainer.getBooleans();

        this.strafeSpeed = floats.read(0);
        this.forwardSpeed = floats.read(1);
        this.jumping = booleans.read(0);
        this.sneaking = booleans.read(1);
    }
}
