package de.derredstoner.anticheat.check.impl.movement.jesus;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@CheckInfo(
        name = "Jesus (A)",
        description = "Checks for walking on water",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class JesusA extends Check {

    public JesusA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(wrapper.isMoving()) {
                if(data.movementProcessor.teleporting
                        || data.movementProcessor.flightTicks < 10
                        || data.movementProcessor.getVelocityV() > 0
                        || data.player.getGameMode().equals(GameMode.SPECTATOR)
                        || !data.movementProcessor.isChunkLoaded) {
                    return;
                }

                Block block = PlayerUtil.getBlockAsync(data.movementProcessor.location.toLocation(data.player).subtract(0,1,0));

                final boolean validBlockType = block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA);

                if(data.movementProcessor.touchingLiquid
                        && !data.movementProcessor.inBlock
                        && validBlockType
                        && !getSolidBlocksAround(data.movementProcessor.location.toLocation(data.player))) {
                    final boolean clientGround = data.movementProcessor.clientGround;

                    final double deltaY = data.movementProcessor.deltaY;
                    final double lastDeltaY = data.movementProcessor.lastDeltaY;

                    if(!data.movementProcessor.clientGround && data.movementProcessor.lastClientGround) {
                        return;
                    }

                    double expectedDeltaY = (lastDeltaY - 0.08) * 0.9800000190734863;

                    if(Math.abs(expectedDeltaY) < 0.005) {
                        expectedDeltaY = 0;
                    }

                    double difference = deltaY - expectedDeltaY;

                    if(difference > 0.005) {
                        if(buffer++ > 0) {
                            flag("difference="+difference+"\ndeltaY="+deltaY+"\nexpected="+expectedDeltaY+"\nlastDeltaY="+lastDeltaY+"\nclientGround="+clientGround);
                            setback();
                        }
                    } else buffer = 0;
                } else buffer = 0;
            }
        }
    }

    private boolean getSolidBlocksAround(Location location) {
        for(double x = location.getX() - 0.4; x <= location.getX() + 0.4; x += 0.4) {
            for(double z = location.getZ() - 0.4; z <= location.getZ() + 0.4; z += 0.4) {
                Block block = PlayerUtil.getBlockAsync(new Location(location.getWorld(), x, location.getY(), z).subtract(0, 1, 0));

                final boolean isLiquid = block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA);

                if(!isLiquid) {
                    return true;
                }
            }
        }

        return false;
    }

}
