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
        name = "Aim (E)",
        description = "Checks for pitch value occurance",
        category = Category.COMBAT,
        subCategory = SubCategory.AIM
)
public class AimE extends Check {

    public AimE(PlayerData data) {
        super(data);
    }

    private final EvictingList<Float> samples = new EvictingList<>(50);

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isRotating() && time() - data.actionProcessor.lastHit < 500L) {
                final float pitch = data.movementProcessor.pitch;

                if(Math.abs(pitch) != 90) {
                    samples.add(pitch);
                }

                if(samples.isFull()) {
                    Map.Entry<Float,Long> highest = samples.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).get();
                    final float value = highest.getKey();
                    final long occurance = highest.getValue();

                    if(value != 0 && occurance > 25) {
                        flag("pitch="+pitch+"\noccurance="+occurance);
                    }
                }
            }
        }
    }

}
