package de.derredstoner.anticheat.check.impl.combat.reach;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import de.derredstoner.anticheat.util.BoundingBox;
import de.derredstoner.anticheat.util.CustomLocation;
import de.derredstoner.anticheat.util.MathUtil;
import de.derredstoner.anticheat.util.RayTrace;
import de.derredstoner.anticheat.util.evicting.EvictingList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CheckInfo(
        name = "Reach (A)",
        description = "Ensures that the client's reach is legitimate",
        category = Category.COMBAT,
        subCategory = SubCategory.REACH
)
public class ReachA extends Check {

    public ReachA(PlayerData data) {
        super(data);
    }

    private boolean attacked;
    private Player target;
    private EvictingList<CustomLocation> targetLocations = new EvictingList<>(40);
    private double buffer2;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                Entity entity = data.player.getWorld().getEntities().stream().filter(en -> en.getEntityId() == wrapper.getEntityId()).findFirst().orElse(null);

                if(entity != null && entity instanceof Player) {
                    if(target != null && target.getEntityId() != entity.getEntityId()) {
                        targetLocations.clear();
                    }

                    target = (Player) entity;
                    attacked = true;
                }
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            if(target != null) {
                double targetX = (int) (target.getLocation().getX() * 32.0);
                double targetY = (int) (target.getLocation().getY() * 32.0);
                double targetZ = (int) (target.getLocation().getZ() * 32.0);
                targetX = targetX / 32.0;
                targetY = targetY / 32.0;
                targetZ = targetZ / 32.0;

                targetLocations.add(new CustomLocation(targetX, targetY, targetZ, data.connectionProcessor.totalTicks));
            }

            if(attacked) {
                attacked = false;

                if(data.connectionProcessor.transactionPing > 1500L
                        || !targetLocations.isFull()
                        || data.player.getVehicle() != null
                        || data.player.isFlying()
                        || data.player.getGameMode().equals(GameMode.CREATIVE)) {
                    return;
                }

                long tick = data.connectionProcessor.totalTicks;
                long pingTicks = getPingInTicks(data.connectionProcessor.transactionPing);

                List<CustomLocation> targetLocs = targetLocations.stream().filter(loc -> Math.abs(tick - loc.getTick() - pingTicks) < 5).collect(Collectors.toList());

                CustomLocation[] eyeLocs = { data.movementProcessor.location.clone(), data.movementProcessor.lastLocation.clone() };

                Pair<Float,Float> look = new Pair<>(data.movementProcessor.yaw, data.movementProcessor.pitch);
                Pair<Float,Float> lastLook = new Pair<>(data.movementProcessor.lastYaw, data.movementProcessor.lastPitch);

                Pair[] lookPairs = { look, lastLook };

                float eyeHeight = data.actionProcessor.sneaking ? (data.protocolVersion >= 441 ? 1.27F : 1.54F) : 1.62F;

                double minDistance = 69420;
                int collided = 0;
                for(CustomLocation eyeLoc : eyeLocs) {
                    eyeLoc.setY(eyeLoc.getY() + eyeHeight);

                    for(Pair<Float,Float> lookPair : lookPairs) {
                        eyeLoc.setYaw(lookPair.getFirst());
                        eyeLoc.setPitch(lookPair.getSecond());

                        for(CustomLocation targetLoc : targetLocs) {
                            BoundingBox boundingBox = new BoundingBox(
                                    targetLoc.getX() - 0.4F,
                                    targetLoc.getX() + 0.4F,
                                    targetLoc.getY(),
                                    targetLoc.getY() + 1.9F,
                                    targetLoc.getZ() - 0.4F,
                                    targetLoc.getZ() + 0.4F
                            );

                            RayTrace rayTrace = new RayTrace(eyeLoc.toLocation(data.player).toVector(), eyeLoc.toLocation(data.player).getDirection());

                            double distance = boundingBox.collidesD(rayTrace, 0.0, 6.5);

                            if(distance != 0) {
                                minDistance = Math.min(minDistance, distance);
                                collided++;
                            }
                        }
                    }
                }

                double maxReach = CheatGuard.getInstance().config.getConfig().getDouble("COMBAT.ReachA.settings.max-reach");

                if(collided > 0) {
                    if(minDistance > maxReach && minDistance < 10) {
                        if(buffer++ > 2) {
                            flag("distance="+minDistance+"\ncollided="+collided);
                        }
                    } else buffer = Math.max(0, buffer - 0.1);

                    buffer2 = 0;
                } else {
                    if(buffer2++ > 10) {
                        data.getChecks().stream().filter(check -> check.getCheckInfo().name().equals("Hitbox (B)")).collect(Collectors.toList()).forEach(check -> check.flag());
                    }
                }
            }
        }
    }

    public int getPingInTicks(long ping) {
        return MathUtil.floor(ping / 50.);
    }

}
