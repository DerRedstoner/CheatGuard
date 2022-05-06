package de.derredstoner.anticheat.data.processor;

import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutTransaction;
import de.derredstoner.anticheat.util.evicting.EvictingMap;

public class ConnectionProcessor {

    private final PlayerData data;

    private final EvictingMap<Short,Long> transactionMap = new EvictingMap<>(20);

    public long transactionPing, lastTransactionPing, totalTicks, lastFlyingDelay, lastFlying, lastLagging;
    public boolean isLagging;
    public short transactionID = Short.MIN_VALUE;

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
        } else if(e instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction wrapper = (WrappedPacketPlayInTransaction) e;

            if(transactionMap.containsKey(wrapper.getTransactionId())) {
                lastTransactionPing = transactionPing;
                transactionPing = System.currentTimeMillis() - transactionMap.get(wrapper.getTransactionId());
            }
        } else if(e instanceof WrappedPacketPlayOutTransaction) {
            WrappedPacketPlayOutTransaction wrapper = (WrappedPacketPlayOutTransaction) e;

            transactionID = wrapper.getTransactionId();
            transactionMap.put(wrapper.getTransactionId(), System.currentTimeMillis());
        }
    }

}
