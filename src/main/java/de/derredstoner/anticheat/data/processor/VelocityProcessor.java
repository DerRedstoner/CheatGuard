package de.derredstoner.anticheat.data.processor;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInTransaction;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutEntityVelocity;
import de.derredstoner.anticheat.util.PlayerUtil;
import de.derredstoner.anticheat.util.Velocity;
import de.derredstoner.anticheat.util.evicting.EvictingMap;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VelocityProcessor {

    private final PlayerData data;

    public double velocityX, velocityY, velocityZ, predictedVelocityY, predictedVelocityH;
    public int velocityTicks;

    private short attackId;
    private EvictingMap<Short,Vector> velocities = new EvictingMap<>(20);

    public VelocityProcessor(PlayerData data) {
        this.data = data;
    }

    public void process(Object e) {
        if(e instanceof WrappedPacketPlayInFlying) {
            if(velocityTicks >= 0) {
                if(predictedVelocityY > 0) {
                    predictedVelocityY -= 0.08D;
                    predictedVelocityY *= 0.98F;
                } else predictedVelocityY = 0;
            }

            if(data.movementProcessor.deltaY == 0.42F) {
                predictedVelocityY = 0;
            }

            if(predictedVelocityY < 0.005) {
                predictedVelocityY = 0;
            }

            ++velocityTicks;

            if(data.player.hasPotionEffect(PotionEffectType.SPEED)) {
                int amplifier = PlayerUtil.getAmplifier(data.player, PotionEffectType.SPEED);
                predictedVelocityH = (predictedVelocityH * Math.pow(0.9, amplifier)) - 0.01;
            }

            if(velocityTicks == 1) {
                if(!data.movementProcessor.clientGround && data.movementProcessor.lastClientGround) {
                    predictedVelocityH = predictedVelocityH - 0.13;
                } else {
                    predictedVelocityH = predictedVelocityH * 0.91 - 0.13;
                }
            }

            predictedVelocityH = Math.max(predictedVelocityH, 0);

            double expectedJump = 0.42F + (PlayerUtil.getAmplifier(data.player, PotionEffectType.JUMP) * 0.1F);

            if(data.movementProcessor.deltaY == expectedJump || predictedVelocityH < 0.005) {
                predictedVelocityH = 0;
            }
        } else if(e instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity event = (WrappedPacketPlayInUseEntity) e;

            if(event.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                attackId = data.connectionProcessor.transactionID;
                predictedVelocityH *= 0.6F;
            }
        } else if(e instanceof WrappedPacketPlayOutEntityVelocity) {
            WrappedPacketPlayOutEntityVelocity wrapper = (WrappedPacketPlayOutEntityVelocity) e;

            if(wrapper.getEntityId() == data.player.getEntityId()) {
                if(wrapper.getVelocityX() != 0 || wrapper.getVelocityY() != 0 || wrapper.getVelocityZ() != 0) {
                    double x = wrapper.getVelocityX() / 8000d;
                    double y = wrapper.getVelocityY() / 8000d;
                    double z = wrapper.getVelocityZ() / 8000d;

                    short transactionId = data.connectionProcessor.transactionID;

                    velocities.put(transactionId, new Vector(x, y, z));
                }
            }
        } else if(e instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction wrapper = (WrappedPacketPlayInTransaction) e;

            if(velocities.containsKey(wrapper.getTransactionId())) {
                Vector velocity = velocities.get(wrapper.getTransactionId());

                if(attackId >= wrapper.getTransactionId()) {
                    velocity.setX(velocity.getX() * 0.6F);
                    velocity.setZ(velocity.getZ() * 0.6F);
                }

                velocityX = velocity.getX();
                velocityY = velocity.getY();
                velocityZ = velocity.getZ();

                predictedVelocityH = Math.hypot(Math.abs(velocity.getX()), Math.abs(velocity.getZ()));
                predictedVelocityY = velocity.getY();

                velocityTicks = 0;

                velocities.remove(wrapper.getTransactionId());
            }
        }
    }

}
