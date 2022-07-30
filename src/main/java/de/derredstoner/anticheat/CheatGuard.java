package de.derredstoner.anticheat;

import com.comphenix.protocol.ProtocolLibrary;
import de.derredstoner.anticheat.command.CheatGuardCommand;
import de.derredstoner.anticheat.data.DataManager;
import de.derredstoner.anticheat.config.Config;
import de.derredstoner.anticheat.handler.watcher.ServerWatcher;
import de.derredstoner.anticheat.listener.BukkitListener;
import de.derredstoner.anticheat.packet.handler.PacketHandler;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CheatGuard extends JavaPlugin {

    private static CheatGuard INSTANCE;

    public Config config;

    @Getter
    private DataManager dataManager;

    public ServerWatcher serverWatcher;

    public final Executor packetThread = Executors.newSingleThreadExecutor();
    public final Executor alertThread = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        this.INSTANCE = this;

        this.config = new Config();

        this.dataManager = new DataManager();

        this.serverWatcher = new ServerWatcher();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketHandler(this));

        getCommand("cheatguard").setExecutor(new CheatGuardCommand());

        if(config.getConfig().getBoolean("settings.metrics")) {
            Metrics metrics = new Metrics(this, 15947);
        }
    }

    @Override
    public void onDisable() {

    }

    public static CheatGuard getInstance() {
        return INSTANCE;
    }

}
