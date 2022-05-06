package de.derredstoner.anticheat.data.processor;

import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutEntityVelocity;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutPosition;
import de.derredstoner.anticheat.util.*;
import de.derredstoner.anticheat.util.evicting.EvictingList;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MovementProcessor {

    protected final EvictingList<Velocity> velocities = new EvictingList<>(100);
    protected final EvictingList<Vector> teleports = new EvictingList<>(100);

    private final PlayerData data;

    public CustomLocation location = new CustomLocation(0,0,0), lastLocation = new CustomLocation(0,0,0), lastGroundLocation;
    public boolean teleporting, smallMove, clientGround, lastClientGround, lastLastClientGround, mathGround, lastMathGround, serverGround, lastServerGround, isChunkLoaded, collidingHorizontally, collidingVerticallyUp, nearWall, touchingClimbable, touchingLiquid, inBlock;
    public float yaw, pitch, lastYaw, lastPitch, lastLastYaw, lastLastPitch, deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch, friction, lastFriction;
    public double deltaX, deltaY, deltaZ, lastDeltaX, lastDeltaY, lastDeltaZ, deltaXZ, lastDeltaXZ;
    public int teleportTicks, nonUpdateTicks, clientAirTicks, clientGroundTicks, serverAirTicks, serverGroundTicks, iceTicks, slimeTicks, flightTicks, webTicks, positionsSinceTeleport;

    public MovementProcessor(PlayerData data) {
        this.data = data;
    }

    public void process(Object e) {
        if(e instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) e;

            teleporting = smallMove = false;

            if(!wrapper.isMoving() && !wrapper.isRotating()) {
                smallMove = true;
                return;
            }

            final double x = wrapper.getX();
            final double y = wrapper.getY();
            final double z = wrapper.getZ();
            final float yaw = wrapper.getYaw();
            final float pitch = wrapper.getPitch();
            final boolean ground = wrapper.isOnGround();

            nonUpdateTicks++;

            if(wrapper.isMoving()) {
                lastLocation = (location.getX() == 0 && location.getY() == 0 && location.getZ() == 0) ? new CustomLocation(x, y, z, yaw, pitch) : location;
                location = new CustomLocation(x, y, z, yaw, pitch);

                this.lastDeltaX = this.deltaX;
                this.lastDeltaY = this.deltaY;
                this.lastDeltaZ = this.deltaZ;

                this.deltaX = location.getX() - lastLocation.getX();
                this.deltaY = location.getY() - lastLocation.getY();
                this.deltaZ = location.getZ() - lastLocation.getZ();

                this.lastDeltaXZ = this.deltaXZ;
                this.deltaXZ = Math.hypot(Math.abs(deltaX), Math.abs(deltaZ));

                if(clientGround) {
                    clientAirTicks = 0;
                    clientGroundTicks++;
                } else {
                    clientAirTicks++;
                    clientGroundTicks = 0;
                }

                final Cuboid horizontalBox = new Cuboid(location).expand(1.0, 0, 1.0);
                final Cuboid nearBox = new Cuboid(location).expand(0.5, 0.55, 0.5);
                final Cuboid aboveHeadBox = new Cuboid(location).expand(1.5, 0, 1.5).move(0, 1.5, 0);
                final Cuboid aboveHeadCuboid = new Cuboid(location).expand(0.5, 0.07, 0.5).move(0, 2.0, 0);
                final Cuboid underCuboid = new Cuboid(location).expand(0.5, 0.07, 0.5).move(0, -1.0, 0);

                this.collidingVerticallyUp = !aboveHeadCuboid.checkBlocks(data.player.getWorld(), material -> material == Material.AIR);
                this.collidingHorizontally = !horizontalBox.checkBlocks(data.player.getWorld(), material -> material == Material.AIR);
                this.touchingClimbable = nearBox.count(data.player.getWorld(), Material.LADDER) > 0 || nearBox.count(data.player.getWorld(), Material.VINE) > 0;
                this.touchingLiquid = nearBox.count(data.player.getWorld(), Material.WATER) > 0 || nearBox.count(data.player.getWorld(), Material.STATIONARY_WATER) > 0 || nearBox.count(data.player.getWorld(), Material.LAVA) > 0 || nearBox.count(data.player.getWorld(), Material.STATIONARY_LAVA) > 0 || underCuboid.count(data.player.getWorld(), Material.WATER) > 0 || underCuboid.count(data.player.getWorld(), Material.STATIONARY_WATER) > 0 || underCuboid.count(data.player.getWorld(), Material.LAVA) > 0 || underCuboid.count(data.player.getWorld(), Material.STATIONARY_LAVA) > 0;

                this.lastServerGround = this.serverGround;
                if(nearBox.checkBlocks(data.player.getWorld(), material -> material == Material.AIR)) {
                    serverAirTicks++;
                    serverGroundTicks = 0;
                    serverGround = true;
                } else {
                    serverAirTicks = 0;
                    serverGroundTicks++;
                    serverGround = false;
                    lastGroundLocation = location;
                }

                if(underCuboid.count(data.player.getWorld(), Material.ICE) > 0 || nearBox.count(data.player.getWorld(), Material.PACKED_ICE) > 0) {
                    iceTicks = 0;
                } else iceTicks++;

                if(underCuboid.count(data.player.getWorld(), Material.SLIME_BLOCK) > 0) {
                    slimeTicks = 0;
                } else slimeTicks++;

                if(nearBox.count(data.player.getWorld(), Material.WEB) > 0 || aboveHeadBox.count(data.player.getWorld(), Material.WEB) > 0) {
                    webTicks = 0;
                } else webTicks++;

                positionsSinceTeleport++;

                lastFriction = friction;
                friction = PlayerUtil.getFriction(data);
                isChunkLoaded = PlayerUtil.isChunkLoaded(location.toLocation(data.player));
            } else {
                nonUpdateTicks = 0;
            }

            if(wrapper.isRotating()) {
                this.lastDeltaYaw = this.deltaYaw;
                this.lastDeltaPitch = this.deltaPitch;

                this.deltaYaw = MathUtil.getCorrectYawDelta(Math.abs(this.lastYaw - yaw));
                this.deltaPitch = pitch - this.lastPitch;

                this.lastLastYaw = this.lastYaw;
                this.lastLastPitch = this.lastPitch;

                this.lastYaw = this.yaw;
                this.lastPitch = this.pitch;

                this.yaw = yaw;
                this.pitch = pitch;
            }

            Vector vec = new Vector(x, y, z);

            if(teleports.contains(vec)) {
                teleports.remove(vec);
                teleporting = true;
            }

            velocities.removeIf(Velocity::isCompleted);

            this.lastLastClientGround = this.lastClientGround;
            this.lastClientGround = this.clientGround;
            this.clientGround = ground;

            this.lastMathGround = this.mathGround;
            this.mathGround = location != null ? MathUtil.isMathGround(location.getY()) : false;

            if(data.player.isFlying()) {
                flightTicks = 0;
            } else flightTicks++;

            if(teleporting) {
                teleportTicks = 0;
                positionsSinceTeleport = 0;
            } else teleportTicks++;
        } else if(e instanceof WrappedPacketPlayOutEntityVelocity) {
            WrappedPacketPlayOutEntityVelocity wrapper = (WrappedPacketPlayOutEntityVelocity) e;

            if(wrapper.getEntityId() == data.player.getEntityId()) {
                double x = wrapper.getVelocityX() / 8000d;
                double y = wrapper.getVelocityY() / 8000d;
                double z = wrapper.getVelocityZ() / 8000d;

                short lastSentTransaction = (short) (data.connectionProcessor.transactionID + 1);

                Velocity velocity = new Velocity(data, lastSentTransaction, x, y, z);

                velocities.add(velocity);
            }
        } else if(e instanceof WrappedPacketPlayOutPosition) {
            WrappedPacketPlayOutPosition wrapper = (WrappedPacketPlayOutPosition) e;

            double posX = wrapper.getX();
            double posY = wrapper.getY();
            double posZ = wrapper.getZ();

            Vector vec = new Vector(posX, posY, posZ);
            teleports.add(vec);
        }
    }

    public double getVelocityH() {
        final List<Velocity> velocitiesCopy = new ArrayList<>(velocities);
        if(velocitiesCopy.size() > 0) {
            return velocitiesCopy.stream().mapToDouble(Velocity::getVelocityH).sum();
        } else return 0;
    }

    public double getVelocityV() {
        final List<Velocity> velocitiesCopy = new ArrayList<>(velocities);
        if(velocitiesCopy.size() > 0) {
            return velocitiesCopy.stream().mapToDouble(Velocity::getVelocityV).sum();
        } else return 0;
    }

}
