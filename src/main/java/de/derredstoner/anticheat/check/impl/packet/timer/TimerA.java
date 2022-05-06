package de.derredstoner.anticheat.check.impl.packet.timer;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutPosition;

@CheckInfo(
        name = "Timer (A)",
        description = "Checks for sped up game timer",
        category = Category.PACKET,
        subCategory = SubCategory.TIMER
)
public class TimerA extends Check {

    public TimerA(PlayerData data) {
        super(data);
    }

    private long balance = -50, lastFlying, ticks;
    private boolean read;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            long now = time();

            if(++ticks > 200 && lastFlying != 0L && data.movementProcessor.deltaXZ > 0) {
                long delta = now - lastFlying;

                balance += 50L;
                balance -= delta;

                if(balance > 50L) {
                    if(buffer++ > 4) {
                        flag("delta="+delta+"\nbalance="+balance);
                        setback();
                    }

                    balance = -50L;
                } else buffer = Math.max(0, buffer - 0.001);

                if(delta > 10 && delta < 49) balance = Math.max(data.connectionProcessor.transactionPing * -1L - 50L, balance);
            } else buffer = Math.max(0, buffer - 0.004);

            lastFlying = now;
        } else if(wrappedPacket instanceof WrappedPacketPlayOutPosition) {
            balance -= 50L;
        }
    }

}
