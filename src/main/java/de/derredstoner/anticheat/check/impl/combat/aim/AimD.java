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
        name = "Aim (D)",
        description = "Ensures the client follows proper horizontal GCD",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM
)
public class AimD extends Check {

    public AimD(PlayerData data) {
        super(data);
    }

    private float minDeltaPitch = 1, lastAccel;
    private int count;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.sensitivityProcessor.sensitivity == 0) return;

            if(wrapper.isRotating() && time() - data.actionProcessor.lastHit < 500L) {
                final float deltaYaw = data.movementProcessor.deltaYaw;
                final float deltaPitch = Math.abs(data.movementProcessor.deltaPitch);

                if(deltaPitch != 0 && deltaPitch < minDeltaPitch) {
                    minDeltaPitch = deltaPitch;
                }

                if(count++ > 20) {
                    count = 0;
                    minDeltaPitch = 1;
                }

                if(minDeltaPitch < 0.1 || count < 20) {
                    return;
                }

                final float accel = Math.abs(data.movementProcessor.lastDeltaYaw - deltaYaw);

                final double offset = MathUtil.EXPANDER;
                final double gcd = MathUtil.gcd((long) (accel * offset), (long) (lastAccel * offset)) / offset;

                final float f1 = (float) data.sensitivityProcessor.sensitivity * 0.6F + 0.2F;
                final float f2 = f1 * f1 * f1 * 1.2F;

                final float mod = (deltaYaw + 0.001F) % f2;

                if(gcd > 0 && gcd < 0.009 && mod > 0.002F && MathUtil.isAlmostEqual(minDeltaPitch, f2) && accel < 5) {
                    if(buffer++ > 5) {
                        flag("gcd="+gcd+"\naccel="+accel+"\nlastAccel="+lastAccel+"\nf2="+f2+"\nmod="+mod);
                    }
                } else if(MathUtil.isAlmostEqual((float) gcd, f2)) {
                    buffer = Math.max(0, buffer - 1);
                } else {
                    buffer = Math.max(0, buffer - 0.1);
                }

                lastAccel = accel;
            }
        }
    }

}
