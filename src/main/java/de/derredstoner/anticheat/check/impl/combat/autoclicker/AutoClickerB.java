package de.derredstoner.anticheat.check.impl.combat.autoclicker;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInArmAnimation;
import de.derredstoner.anticheat.util.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(
        name = "AutoClicker (B)",
        description = "Checks for stable click pattern",
        category = Category.COMBAT,
        subCategory = SubCategory.AUTOCLICKER
)
public class AutoClickerB extends Check {

    public AutoClickerB(PlayerData data) {
        super(data);
    }

    private long lastClick;
    private double lastVariance, lastStd;
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInArmAnimation) {
            if(data.actionProcessor.isDigging) {
                return;
            }

            long delay = time() - lastClick;
            int ticks = (int) (delay+20) / 50;

            if(ticks < 5) {
                samples.add(ticks);
            }

            if(samples.size() == 50) {
                double variance = MathUtil.getVariance(samples);
                double std = MathUtil.getStandardDeviation(samples);

                if(Math.abs(variance - lastVariance) < 0.1 && Math.abs(std - lastStd) < 0.05 && data.cps > 5) {
                    flag("varianceDiff="+(float)Math.abs(variance - lastVariance)+"\nstdDiff="+(float)Math.abs(std - lastStd)+"\ncps="+data.cps);
                }

                lastVariance = variance;
                lastStd = std;

                samples.clear();
            }

            lastClick = time();
        }
    }

}
