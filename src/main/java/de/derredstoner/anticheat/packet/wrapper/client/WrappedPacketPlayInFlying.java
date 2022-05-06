package de.derredstoner.anticheat.packet.wrapper.client;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInFlying extends WrappedPacket {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean onGround;
    private final boolean moving;
    private final boolean rotating;

    public WrappedPacketPlayInFlying(PacketContainer packetContainer) {
        StructureModifier<Double> doubles = packetContainer.getDoubles();
        StructureModifier<Float> floats = packetContainer.getFloat();
        StructureModifier<Boolean> booleans = packetContainer.getBooleans();

        this.x = doubles.read(0);
        this.y = doubles.read(1);
        this.z = doubles.read(2);

        this.yaw = floats.read(0);
        this.pitch = floats.read(1);

        this.onGround = booleans.read(0);
        this.moving = booleans.read(1);
        this.rotating = booleans.read(2);
    }

}
