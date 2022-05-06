package de.derredstoner.anticheat.packet.wrapper.client;

import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayInArmAnimation extends WrappedPacket {

    private final long timestamp;

    public WrappedPacketPlayInArmAnimation(PacketContainer packetContainer) {
        if(CheatGuard.getInstance().serverWatcher.is1_8()) {
            this.timestamp = packetContainer.getLongs().read(0);
        } else {
            timestamp = -1;
        }
    }
}
