package de.derredstoner.anticheat.packet.wrapper.client;

import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedPacketPlayInBlockPlace extends WrappedPacket {

    private final BlockPosition blockPosition;
    private final int face;
    private final ItemStack itemStack;
    private final float facingX;
    private final float facingY;
    private final float facingZ;

    public WrappedPacketPlayInBlockPlace(PacketContainer packetContainer) {
        if(CheatGuard.getInstance().serverWatcher.is1_8()) {
            this.blockPosition = packetContainer.getBlockPositionModifier().read(0);
            this.face = packetContainer.getIntegers().read(0);
            this.itemStack = packetContainer.getItemModifier().read(0);

            StructureModifier<Float> floats = packetContainer.getFloat();

            this.facingX = floats.read(0);
            this.facingY = floats.read(1);
            this.facingZ = floats.read(2);
        } else {
            this.blockPosition = null;
            this.face = 0;
            this.itemStack = null;

            this.facingX = 0;
            this.facingY = 0;
            this.facingZ = 0;
        }
    }

}
