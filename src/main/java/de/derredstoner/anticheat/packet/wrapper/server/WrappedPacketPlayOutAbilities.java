package de.derredstoner.anticheat.packet.wrapper.server;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutAbilities extends WrappedPacket {

    private final boolean invulnerable;
    private final boolean flying;
    private final boolean canFly;
    private final boolean canInstantlyBuild;
    private final float flySpeed;
    private final float walkSpeed;

    public WrappedPacketPlayOutAbilities(PacketContainer packetContainer) {
        StructureModifier<Boolean> booleans = packetContainer.getBooleans();
        StructureModifier<Float> floats = packetContainer.getFloat();

        this.invulnerable = booleans.read(0);
        this.flying = booleans.read(1);
        this.canFly = booleans.read(2);
        this.canInstantlyBuild = booleans.read(3);
        this.flySpeed = floats.read(0);
        this.walkSpeed = floats.read(1);
    }
}
