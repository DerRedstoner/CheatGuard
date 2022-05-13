package de.derredstoner.anticheat.check.impl.player.scaffold;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInEntityAction;
import org.bukkit.Material;

@CheckInfo(
        name = "Scaffold (F)",
        description = "Checks for invalid sneak when bridging",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldF extends Check {

    public ScaffoldF(PlayerData data) {
        super(data);
    }

    private long lastPlaceTicks;
    private boolean checking;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(wrapper.getBlockPosition().getY() == -1) {
                return;
            }

            long currentTick = data.connectionProcessor.totalTicks;
            long delay = currentTick - lastPlaceTicks;

            if(isBridging() && delay < 300L && data.movementProcessor.deltaXZ > data.player.getWalkSpeed()) {
                checking = true;
            }

            lastPlaceTicks = data.connectionProcessor.totalTicks;
        } else if(wrappedPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SNEAKING) {
                long sneakDelay = data.connectionProcessor.totalTicks - lastPlaceTicks;

                if(sneakDelay <= 1 && checking) {
                    if(buffer++ > 2) {
                        flag("delay="+sneakDelay);
                    }
                } else buffer = 0;
            }
        }
    }

    public boolean isBridging() {
        return data.player.getLocation().clone().subtract(0, 2.5, 0).getBlock().getType() == Material.AIR
                && (data.movementProcessor.pitch > 50 || data.movementProcessor.lastPitch > 50);
    }

}
