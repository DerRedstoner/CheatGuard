package de.derredstoner.anticheat.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.CheckManager;
import de.derredstoner.anticheat.data.processor.*;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerData {

    public final Player player;

    private List<Check> checks;

    public final ActionProcessor actionProcessor = new ActionProcessor(this);
    public final ConnectionProcessor connectionProcessor = new ConnectionProcessor(this);
    public final SensitivityProcessor sensitivityProcessor = new SensitivityProcessor(this);
    public final MovementProcessor movementProcessor = new MovementProcessor(this);
    public final VelocityProcessor velocityProcessor = new VelocityProcessor(this);

    public int protocolVersion, cps;
    public String version;

    public boolean alerts = false;

    public PlayerData(Player player) {
        this.player = player;
        this.checks = CheckManager.getChecks(this);

        if(CheatGuard.getInstance().getServer().getPluginManager().isPluginEnabled("ViaVersion")) {
            ViaAPI api = Via.getAPI();
            this.protocolVersion = api.getPlayerVersion(player);

            if(protocolVersion == 47) {
                this.version = "1.8";
            } else if(protocolVersion >= 48 && protocolVersion <= 110) {
                this.version = "1.9";
            } else if(protocolVersion >= 201 && protocolVersion <= 210) {
                this.version = "1.10";
            } else if(protocolVersion >= 301 && protocolVersion <= 316) {
                this.version = "1.11";
            } else if(protocolVersion >= 317 && protocolVersion <= 340) {
                this.version = "1.12";
            } else if(protocolVersion >= 341 && protocolVersion <= 404) {
                this.version = "1.13";
            } else if(protocolVersion >= 441 && protocolVersion <= 498) {
                this.version = "1.14";
            } else if(protocolVersion >= 550 && protocolVersion <= 578) {
                this.version = "1.15";
            } else if(protocolVersion >= 701 && protocolVersion <= 754) {
                this.version = "1.16";
            } else if(protocolVersion >= 755 && protocolVersion <= 756) {
                this.version = "1.17";
            } else if(protocolVersion >= 757 && protocolVersion <= 758) {
                this.version = "1.18";
            } else {
                this.version = "Unknown";
            }
        }

        if(player.isOp() || player.hasPermission("cheatguard.alerts")) {
            this.alerts = true;
        }
    }

    public List<Check> getChecks() {
        return checks;
    }
}
