package de.derredstoner.anticheat.check.impl.combat.aim;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.evicting.EvictingList;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@CheckInfo(
        name = "Aim (F)",
        description = "Checks for spamming small pitch changes",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM
)
public class AimF extends Check {

    public AimF(PlayerData data) {
        super(data);
    }

    private double lastDeltaPitch;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isRotating()) {
                double deltaPitch = Math.abs(data.movementProcessor.deltaPitch);

                if(deltaPitch > 0 && deltaPitch < 0.009 && deltaPitch != lastDeltaPitch) {
                    if(buffer++ > 20) {
                        flag("deltaPitch="+deltaPitch+"\nlastDeltaPitch="+lastDeltaPitch);
                    }
                } else buffer = 0;

                lastDeltaPitch = deltaPitch;
            }
        }
    }

}
