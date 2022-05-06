package de.derredstoner.anticheat.data;

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

    public boolean alerts = false;

    public PlayerData(Player player) {
        this.player = player;
        this.checks = CheckManager.getChecks(this);

        if(player.isOp() || player.hasPermission("cheatguard.alerts")) {
            this.alerts = true;
        }
    }

    public List<Check> getChecks() {
        return checks;
    }
}
