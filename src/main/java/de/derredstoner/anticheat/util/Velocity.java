package de.derredstoner.anticheat.util;

import de.derredstoner.anticheat.data.PlayerData;
import lombok.Getter;

@Getter
public class Velocity {

    public double velocityH;
    public double velocityV;
    public double velocityX;
    public double velocityZ;
    private final PlayerData playerData;
    private final short transaction;
    private int startTick = -1;
    private int completedTick = -1;

    public Velocity(PlayerData playerData, short transaction, double x, double y, double z) {
        this.playerData = playerData;
        this.velocityX = x;
        this.velocityZ = z;
        this.velocityH = Math.hypot(x, z);
        this.velocityV = y;
        this.transaction = transaction;

        start();
    }

    public void start() {
        int ticks = (int) playerData.connectionProcessor.totalTicks;

        this.startTick = ticks;
        this.completedTick = (int) (ticks + ((velocityH / 2 + 2) * 15));
    }

    public boolean isCompleted() {
        return completedTick != -1 && playerData.connectionProcessor.totalTicks > completedTick;
    }

}
