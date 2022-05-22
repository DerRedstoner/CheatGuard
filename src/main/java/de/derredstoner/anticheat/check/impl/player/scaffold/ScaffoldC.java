package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.util.MathUtil;
import de.derredstoner.anticheat.util.evicting.EvictingList;
import org.bukkit.Material;

@CheckInfo(
        name = "Scaffold (C)",
        description = "Checks for similar placement delays",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldC extends Check {

    public ScaffoldC(PlayerData data) {
        super(data);
    }

    private EvictingList<Integer> samples = new EvictingList<>(10);
    private long lastPlace;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(wrapper.getBlockPosition() == null || wrapper.getBlockPosition().getY() == -1) {
                return;
            }

            long timestamp = time();
            long delay = timestamp - lastPlace;

            double deltaXZ = data.movementProcessor.deltaXZ;

            if(deltaXZ < data.player.getWalkSpeed()) {
                samples.clear();
            }

            if(isBridging() && delay < 500L) {
                samples.add((int) delay);

                if(samples.isFull()) {
                    double deviation = MathUtil.getStandardDeviation(samples);
                    double average = samples.stream().mapToDouble(s -> s).average().getAsDouble();

                    if(deviation < 10 && average < 280) {
                        flag("deviation="+deviation+"\naverage="+average);
                    }
                }
            }

            lastPlace = timestamp;
        }
    }

    public boolean isBridging() {
        return data.player.getLocation().clone().subtract(0, 2.5, 0).getBlock().getType() == Material.AIR
                && (data.movementProcessor.pitch > 50 || data.movementProcessor.lastPitch > 50);
    }

}
