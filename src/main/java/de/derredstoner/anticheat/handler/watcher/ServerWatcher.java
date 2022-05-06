package de.derredstoner.anticheat.handler.watcher;

import com.viaversion.viaversion.api.Via;
import de.derredstoner.anticheat.CheatGuard;
import org.bukkit.Bukkit;

public class ServerWatcher {

    private long serverTicks, lastTick;
    private double tps = 20, exemptTps;;
    private boolean lagging;
    private String version;

    public ServerWatcher() {
        tick();
        watchTps();

        this.exemptTps = CheatGuard.getInstance().config.getConfig().getDouble("settings.exempt-tps");
        this.version = Via.getPlatform().getPlatformVersion().split(" ")[Via.getPlatform().getPlatformVersion().split(" ").length-1].replace(")", "");
    }

    private void tick() {
        Bukkit.getScheduler().runTaskTimer(CheatGuard.getInstance(), new Runnable() {
            @Override
            public void run() {
                ++serverTicks;
                watchTps();
            }
        }, 0L, 0L);
    }

    private void watchTps() {
        long delta = System.currentTimeMillis() - this.lastTick;

        this.tps = Math.min(21, ((this.tps * 4d) + ((delta / 50.d) * 20.d)) / 5.d);
        this.lagging = this.tps < this.exemptTps;

        this.lastTick = System.currentTimeMillis();
    }

    public boolean is1_8() {
        return getVersion().startsWith("1.8");
    }

    public boolean is1_9orAbove() {
        return !getVersion().startsWith("1.8");
    }

    public boolean is1_12orAbove() {
        return getVersion().startsWith("1.12")
                || getVersion().startsWith("1.13")
                || getVersion().startsWith("1.14")
                || getVersion().startsWith("1.15")
                || getVersion().startsWith("1.16")
                || getVersion().startsWith("1.17")
                || getVersion().startsWith("1.18");
    }

    public boolean is1_13orAbove() {
        return getVersion().startsWith("1.13")
                || getVersion().startsWith("1.14")
                || getVersion().startsWith("1.15")
                || getVersion().startsWith("1.16")
                || getVersion().startsWith("1.17")
                || getVersion().startsWith("1.18");
    }

    public boolean isLagging() {
        return this.lagging;
    }

    public long getServerTicks() {
        return this.serverTicks;
    }

    public double getTps() {
        return this.tps;
    }

    public String getVersion() {
        return this.version;
    }

}
