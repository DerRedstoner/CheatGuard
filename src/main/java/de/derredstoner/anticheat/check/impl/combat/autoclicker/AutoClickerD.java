package de.derredstoner.anticheat.check.impl.combat.autoclicker;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInArmAnimation;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(
        name = "AutoClicker (D)",
        description = "Checks for stable click delays",
        category = Category.COMBAT,
        subCategory = SubCategory.AUTOCLICKER
)
public class AutoClickerD extends Check {

    public AutoClickerD(PlayerData data) {
        super(data);
    }

    private int ticks;
    private double lastVariance, buffer2;
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            ticks++;
        } else if(wrappedPacket instanceof WrappedPacketPlayInArmAnimation) {
            if(data.actionProcessor.isDigging) {
                return;
            }

            if(ticks < 5 && data.movementProcessor.deltaXZ > 0.1 && data.movementProcessor.lastDeltaXZ > 0.1) {
                samples.add(ticks);
            }

            if(samples.size() == 50) {
                double variance = MathUtil.getVariance(samples);
                double std = MathUtil.getStandardDeviation(samples);

                if(variance < 11.0 && Math.abs(variance - lastVariance) < 1.0 && std < 5.0 && data.cps > 3) {
                    if(buffer++ > 1) {
                        flag("variance="+variance+"\nlastVariance="+lastVariance+"\nstd="+std+"\ncps="+data.cps);
                    }
                } else buffer = 0;

                if(variance > 0 && variance < 25.0 && std < 4.0 && data.cps > 12) {
                    if(buffer2++ > 1) {
                        flag("variance="+variance+"\nstd="+std+"\ncps="+data.cps);
                    }
                } else buffer2 = 0;

                lastVariance = variance;

                samples.clear();
            }

            ticks = 0;
        }
    }

}
