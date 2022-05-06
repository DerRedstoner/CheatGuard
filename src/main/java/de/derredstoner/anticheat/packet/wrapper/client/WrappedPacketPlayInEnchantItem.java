package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInEnchantItem extends WrappedPacket {

    private final int windowId;
    private final int button;

    public WrappedPacketPlayInEnchantItem(PacketContainer packetContainer) {
        StructureModifier<Integer> integers = packetContainer.getIntegers();

        this.windowId = integers.read(0);
        this.button = integers.read(1);
    }
}
