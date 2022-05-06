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
        name = "Aim (A)",
        description = "Ensures that the client follows proper GCD",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM
)
public class AimA extends Check {

    public AimA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isRotating() && time() - data.actionProcessor.lastHit < 250L) {
                final float deltaPitch = data.movementProcessor.deltaPitch;
                final float lastDeltaPitch = data.movementProcessor.lastDeltaPitch;

                final long gcd = MathUtil.gcd((long) (MathUtil.EXPANDER * Math.abs(deltaPitch)), (long) (MathUtil.EXPANDER * Math.abs(lastDeltaPitch)));

                if(gcd > 0 && gcd < 131072L) {
                    if(buffer++ > 10) {
                        flag("gcd="+gcd);
                    }
                } else buffer = Math.max(0, buffer - 2);
            }
        }
    }

}
