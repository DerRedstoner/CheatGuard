package de.derredstoner.anticheat.check.impl.combat.killaura;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;

@CheckInfo(
        name = "KillAura (A)",
        description = "Checks for post attack packet",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraA extends Check {

    public KillAuraA(PlayerData data) {
        super(data);
    }

    private long lastFlying, lastFlyingDelay;
    private double average = 25L;
    private int hits;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            final double delta = time() - lastFlying;

            average = ((average * 9) + delta) / 10;

            if(lastFlyingDelay > 10L && lastFlyingDelay < 90L) {
                if(average < 10 && hits > 10) {
                    flag("delta="+(float)average);
                    average = 20;
                }
            }

            hits++;
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            lastFlyingDelay = time() - lastFlying;
            lastFlying = time();
        }
    }

}
