package de.derredstoner.anticheat.config;

import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.CheckManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private FileConfiguration config;
    private static File configf;

    public Config() {
        createConfig();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void createConfig() {
        configf = new File(CheatGuard.getInstance().getDataFolder(), "config.yml" );

        if(!configf.exists()) {
            configf.getParentFile().mkdirs();
            CheatGuard.getInstance().saveResource( "config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configf);
        try {
            config.load(configf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
