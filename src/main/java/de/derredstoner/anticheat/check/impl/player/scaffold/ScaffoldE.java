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
        name = "Scaffold (E)",
        description = "Checks for similar sneak delays",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldE extends Check {

    public ScaffoldE(PlayerData data) {
        super(data);
    }

    private long lastPlaceTicks, lastSneakTicks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            if(wrapper.getBlockPosition() == null || wrapper.getBlockPosition().getY() == -1) {
                return;
            }

            long currentTick = data.connectionProcessor.totalTicks;
            long delay = currentTick - lastPlaceTicks;

            if(isBridging() && delay < 500L) {
                long sneakDelay = currentTick - lastSneakTicks;

                if(sneakDelay <= 1) {
                    if(buffer++ > 1) {
                        flag("delay="+sneakDelay);
                    }
                } else buffer = 0;
            }

            lastPlaceTicks = data.connectionProcessor.totalTicks;
        } else if(wrappedPacket instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SNEAKING) {
                lastSneakTicks = data.connectionProcessor.totalTicks;
            }
        }
    }

    public boolean isBridging() {
        return data.player.getLocation().clone().subtract(0, 2.5, 0).getBlock().getType() == Material.AIR
                && (data.movementProcessor.pitch > 50 || data.movementProcessor.lastPitch > 50);
    }

}
