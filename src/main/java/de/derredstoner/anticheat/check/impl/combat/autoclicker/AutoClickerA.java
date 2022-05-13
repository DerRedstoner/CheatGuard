package de.derredstoner.anticheat.check.impl.combat.autoclicker;

import com.comphenix.protocol.wrappers.Pair;
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
import java.util.List;

@CheckInfo(
        name = "AutoClicker (A)",
        description = "Checks for high CPS with low outliers",
        category = Category.COMBAT,
        subCategory = SubCategory.AUTOCLICKER
)
public class AutoClickerA extends Check {

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    private int ticks, cps, cpsTicks;
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            ticks++;

            if(cpsTicks++ >= 20) {
                data.cps = cps;
                cpsTicks = cps = 0;
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInArmAnimation) {
            if(data.actionProcessor.isDigging) {
                return;
            }

            cps++;

            if(ticks < 4 && data.cps >= 10 && data.movementProcessor.deltaXZ > 0.1 && data.movementProcessor.lastDeltaXZ > 0.1) {
                samples.add(ticks);
            }

            if(samples.size() == 20) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                double deviation = MathUtil.getStandardDeviation(samples);
                double outliers = outlierPair.getFirst().size() + outlierPair.getSecond().size();

                if(deviation < 0.7 && outliers < 2) {
                    flag("deviation="+deviation+"\noutliers="+outliers+"\ncps="+data.cps);
                }

                samples.clear();
            }

            ticks = 0;
        }
    }

}
