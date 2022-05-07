package de.derredstoner.anticheat;

import com.comphenix.protocol.ProtocolLibrary;
import de.derredstoner.anticheat.command.CheatGuardCommand;
import de.derredstoner.anticheat.data.DataManager;
import de.derredstoner.anticheat.config.Config;
import de.derredstoner.anticheat.handler.watcher.ServerWatcher;
import de.derredstoner.anticheat.listener.BukkitListener;
import de.derredstoner.anticheat.packet.handler.PacketHandler;
import dev.thomazz.pledge.api.Pledge;
import lombok.Getter;
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

    private Pledge pledge;

    public final Executor packetThread = Executors.newSingleThreadExecutor();
    public final Executor alertThread = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        this.INSTANCE = this;

        this.config = new Config();

        this.dataManager = new DataManager();

        this.serverWatcher = new ServerWatcher();

        this.pledge = Pledge.build().range(Short.MIN_VALUE, (short) 0);
        this.pledge.start(this);

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketHandler(this));

        getCommand("cheatguard").setExecutor(new CheatGuardCommand());
    }

    @Override
    public void onDisable() {

    }

    public static CheatGuard getInstance() {
        return INSTANCE;
    }

}
