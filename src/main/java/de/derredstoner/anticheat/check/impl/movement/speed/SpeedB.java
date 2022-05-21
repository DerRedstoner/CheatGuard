package de.derredstoner.anticheat.check.impl.movement.speed;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Speed (B)",
        description = "Ensures that the client follows mc speed laws",
        category = Category.MOVEMENT,
        subCategory = SubCategory.MOVE
)
public class SpeedB extends Check {

    public SpeedB(PlayerData data) {
        super(data);
    }

    private int gt, at, bypassTicks;
    private float lastWalkSpeed, lastSlowLevel, lastSpeedLevel, lastFriction;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying moveEvent = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.movementProcessor.teleporting
                    || data.player.getAllowFlight()
                    || data.movementProcessor.touchingLiquid
                    || data.actionProcessor.elytraFlying) {
                return;
            }

            // TODO: Exempt pistons

            if(moveEvent.isMoving()) {
                final double deltaXZ = data.movementProcessor.deltaXZ;
                final double lastDeltaXZ = data.movementProcessor.lastDeltaXZ;
                final double deltaY = data.movementProcessor.deltaY;

                if(data.movementProcessor.clientGround) {
                    gt++;
                    at = 0;
                } else {
                    at++;
                    gt = 0;
                }

                float expectedJump = 0.42F + PlayerUtil.getAmplifier(data.player, PotionEffectType.JUMP) * 0.1F;

                int groundTicks = data.movementProcessor.clientGroundTicks;
                int airTicks = data.movementProcessor.clientAirTicks;

                if(at == 1 || deltaY == expectedJump) {
                    groundTicks = 0;
                    airTicks = 1;
                }

                float friction = data.movementProcessor.friction;

                final float speedLevel = PlayerUtil.getAmplifier(data.player, PotionEffectType.SPEED);
                final float slownessLevel = PlayerUtil.getAmplifier(data.player, PotionEffectType.SLOW);

                if(speedLevel < lastSpeedLevel || slownessLevel > lastSlowLevel || data.player.getWalkSpeed() < lastWalkSpeed) {
                    bypassTicks = 10;
                }

                if(friction < lastFriction) {
                    friction = lastFriction;
                }

                double movementAddition = 0.13;
                movementAddition += movementAddition * -0.15 * slownessLevel;
                movementAddition += movementAddition * 0.2 * speedLevel;

                double prediction = 0;
                if(groundTicks == 1) {
                    prediction = lastDeltaXZ * 0.91F + 0.2F + movementAddition;
                } else if(groundTicks == 2) {
                    prediction = lastDeltaXZ * 0.91F + movementAddition;
                } else if(groundTicks >= 3) {
                    prediction = lastDeltaXZ * 0.91F * friction + movementAddition;
                } else if(airTicks == 1) {
                    if(lastDeltaXZ < 0.2) {
                        prediction = lastDeltaXZ * 0.91F + 0.2F + movementAddition + 0.005;
                    } else {
                        prediction = lastDeltaXZ * 0.91F + 0.2F + movementAddition;
                    }
                } else if(airTicks == 2) {
                    if(data.movementProcessor.slimeTicks < 5) {
                        prediction = lastDeltaXZ + 0.026F;
                    } else {
                        prediction = lastDeltaXZ * 0.91F + 0.026F;
                    }
                } else if(airTicks >= 3) {
                    prediction = lastDeltaXZ * 0.91F + 0.026F;
                }

                if(data.player.getWalkSpeed() > 0.2) {
                    prediction += (data.player.getWalkSpeed() - 0.2F) * 1.6F;
                }

                if(data.velocityProcessor.velocityTicks <= 1) {
                    prediction += data.velocityProcessor.predictedVelocityH;
                }

                prediction += 1E-4;

                final double speed = deltaXZ / prediction;

                if(bypassTicks <= 0 && prediction > 0.03 && deltaXZ > data.player.getWalkSpeed() + 0.026F) {
                    if(speed > 1) {
                        if(buffer++ > 1) {
                            flag("speed="+(speed*100.0)+"%\ngt="+groundTicks+"\nat="+airTicks+"\nf="+friction+"\nxz="+deltaXZ+"\nlxz="+lastDeltaXZ+"\np="+prediction+"\nmA="+movementAddition+"\ndeltaY="+(float)deltaY+"\na="+at+"\ng="+gt);
                            setback();
                        }
                    } else buffer = Math.max(0, buffer - 0.005);
                }

                lastWalkSpeed = data.player.getWalkSpeed();
                lastSpeedLevel = speedLevel;
                lastSlowLevel = slownessLevel;

                lastFriction = friction;
                bypassTicks = Math.max(0, bypassTicks - 1);
            }
        }
    }

}
