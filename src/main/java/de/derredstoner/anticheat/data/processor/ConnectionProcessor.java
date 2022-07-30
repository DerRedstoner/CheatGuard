package de.derredstoner.anticheat.data.processor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutTransaction;
import de.derredstoner.anticheat.util.evicting.EvictingMap;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class ConnectionProcessor {

    private final PlayerData data;

    private final EvictingMap<Short,Long> transactionMap = new EvictingMap<>(20);

    public long transactionPing, lastTransactionPing, totalTicks, lastFlyingDelay, lastFlying, lastLagging;
    public boolean isLagging, sentTransaction;
    public short transactionID = -1;

    public ConnectionProcessor(PlayerData data) {
        this.data = data;
    }

    public void process(Object e) {
        if(e instanceof WrappedPacketPlayInFlying) {
            totalTicks++;

            long now = System.currentTimeMillis();

            lastFlyingDelay = now - lastFlying;
            lastFlying = now;

            if(lastFlyingDelay < 10L || lastFlyingDelay > 90L) {
                isLagging = true;
                lastLagging = totalTicks;
            } else isLagging = false;

            if(transactionID <= Short.MIN_VALUE) transactionID = -1;
            sentTransaction = false;
            try {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.TRANSACTION);
                packet.getShorts().write(0, transactionID--);
                packet.getBooleans().write(0, false);
                packet.getIntegers().write(0, 0);
                ProtocolLibrary.getProtocolManager().sendServerPacket(data.player, packet);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
        } else if(e instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction wrapper = (WrappedPacketPlayInTransaction) e;

            if(transactionMap.containsKey(wrapper.getTransactionId())) {
                lastTransactionPing = transactionPing;
                transactionPing = System.currentTimeMillis() - transactionMap.get(wrapper.getTransactionId());
            }
        } else if(e instanceof WrappedPacketPlayOutTransaction) {
            WrappedPacketPlayOutTransaction wrapper = (WrappedPacketPlayOutTransaction) e;

            transactionMap.put(wrapper.getTransactionId(), System.currentTimeMillis());
            sentTransaction = true;
        }
    }

}
