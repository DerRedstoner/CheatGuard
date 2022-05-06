package de.derredstoner.anticheat.packet.handler;

import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.data.DataManager;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.ClassWrapper;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;

public class PacketHandler extends PacketAdapter {

    private final DataManager dataManager;

    public PacketHandler(CheatGuard cheatGuard) {
        super(cheatGuard, ListenerPriority.NORMAL, ClassWrapper.getProcessedPackets());

        this.dataManager = cheatGuard.getDataManager();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PlayerData data = dataManager.getData(event.getPlayer());
        WrappedPacket wrappedPacket = ClassWrapper.wrapPacket(event.getPacketType(), event.getPacket());

        handle(data, wrappedPacket);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PlayerData data = dataManager.getData(event.getPlayer());

        if(event.isPlayerTemporary()) {
            return;
        }

        WrappedPacket wrappedPacket = ClassWrapper.wrapPacket(event.getPacketType(), event.getPacket());

        handle(data, wrappedPacket);
    }

    public void handle(PlayerData data, WrappedPacket wrappedPacket) {
        if(data != null) {
            CheatGuard.getInstance().packetThread.execute(() -> {
                data.actionProcessor.process(wrappedPacket);
                data.sensitivityProcessor.process(wrappedPacket);
                data.connectionProcessor.process(wrappedPacket);
                data.movementProcessor.process(wrappedPacket);
                data.velocityProcessor.process(wrappedPacket);

                if(!CheatGuard.getInstance().serverWatcher.isLagging()) {
                    data.getChecks().stream().forEach(check -> check.handle(wrappedPacket));
                }
            });
        }
    }

}
