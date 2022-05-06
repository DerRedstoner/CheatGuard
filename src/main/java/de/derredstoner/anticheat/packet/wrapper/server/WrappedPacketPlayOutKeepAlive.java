package de.derredstoner.anticheat.packet.wrapper.server;

import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import lombok.Getter;

@Getter
public class WrappedPacketPlayOutKeepAlive extends WrappedPacket {

    private final long key;

    public WrappedPacketPlayOutKeepAlive(PacketContainer packetContainer) {
        if(CheatGuard.getInstance().serverWatcher.is1_12orAbove()) {
            this.key = packetContainer.getLongs().read(0);
        } else {
            this.key = packetContainer.getIntegers().read(0);
        }
    }



}
