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
        name = "AutoClicker (C)",
        description = "Checks for invalid variance",
        category = Category.COMBAT,
        subCategory = SubCategory.AUTOCLICKER
)
public class AutoClickerC extends Check {

    public AutoClickerC(PlayerData data) {
        super(data);
    }

    private int ticks;
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

            if(samples.size() == 20) {
                double variance = MathUtil.getVariance(samples);

                if(data.cps >= 10) {
                    if(variance < 1.75) {
                        if(buffer++ > 0) {
                            flag("cps="+data.cps+"\nvariance="+variance);
                        }
                    } else if(buffer > 0) {
                        buffer -= 0.2;
                    }
                }

                if(data.cps >= 11 && variance == 0.0) {
                    flag("cps="+data.cps);
                }

                samples.clear();
            }

            ticks = 0;
        }
    }

}
