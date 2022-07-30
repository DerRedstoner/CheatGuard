package de.derredstoner.anticheat.check.impl.combat.aim;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;

@CheckInfo(
        name = "Aim (C)",
        description = "Checks for invalid rotations",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM,
        experimental = true
)
public class AimC extends Check {

    public AimC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.movementProcessor.teleporting) {
                return;
            }

            if(wrapper.isRotating() && time() - data.actionProcessor.lastHit < 500L) {
                final float deltaPitch = Math.abs(data.movementProcessor.deltaPitch);
                final float lastDeltaPitch = Math.abs(data.movementProcessor.lastDeltaPitch);

                if(Math.abs(wrapper.getPitch()) > 70) {
                    return;
                }

                if(deltaPitch > 1 && lastDeltaPitch > 1) {
                    final long expanded = (long) (deltaPitch * MathUtil.EXPANDER);
                    final long lastExpanded = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                    final long gcd = MathUtil.gcd(expanded, lastExpanded);

                    final double divisor = gcd / MathUtil.EXPANDER;

                    final double sens = data.sensitivityProcessor.sensitivityPercentage;

                    if(divisor < 0.005 && sens > 20 && deltaPitch < 20 && lastDeltaPitch < 20) {
                        if(buffer++ > 10) {
                            flag("divisor="+divisor+"\nsens="+sens+"\ndeltaPitch="+deltaPitch+"\nlastDeltaPitch="+lastDeltaPitch);
                        }
                    } else buffer = Math.max(0, buffer - 0.25);
                }
            }
        }
    }

}
