package de.derredstoner.anticheat.check;

import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.util.CustomLocation;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Check {

    protected PlayerData data;

    @Getter
    protected final CheckInfo checkInfo;

    private final boolean enabled, bannable;
    private final int banVL;

    protected double buffer;
    private int violations;

    public Check(PlayerData data) {
        this.data = data;

        this.checkInfo = this.getClass().getAnnotation(CheckInfo.class);

        final String checkName = checkInfo.name().replaceAll("[^a-zA-Z]+", "");

        this.banVL = CheatGuard.getInstance().config.getConfig().getInt(checkInfo.category().name()+"."+checkName+".banVL");
        this.bannable = CheatGuard.getInstance().config.getConfig().getBoolean(checkInfo.category().name()+"."+checkName+".bannable");
        this.enabled = CheatGuard.getInstance().config.getConfig().getBoolean(checkInfo.category().name()+"."+checkName+".enabled");
    }

    public void flag() {
        this.flag("");
    }

    public void flag(String debug) {
        if(!enabled || CheatGuard.getInstance().serverWatcher.getServerTicks() < 20) {
            return;
        }

        this.violations++;
        this.buffer = Math.max(0, buffer - 1);

        CheatGuard.getInstance().alertThread.execute(() -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = CheatGuard.getInstance().getDataManager().getData(player);

                if(playerData.alerts || CheatGuard.getInstance().config.getConfig().getBoolean("settings.test-mode")) {
                    String message = "";
                    if(checkInfo.experimental()) {
                        message = CheatGuard.getInstance().config.getConfig().getString("messages.alerts-experimental");
                    } else {
                        message = CheatGuard.getInstance().config.getConfig().getString("messages.alerts");
                    }

                    String bar = "";
                    int barsToAdd = (int) (Math.min(1, violations / (double) banVL) * 20.0);
                    for(int i = 0; i < barsToAdd; i++) {
                        bar = bar+"§c|";
                    }
                    if(violations != banVL) {
                        for(int i = 0; i < 20 - barsToAdd; i++) {
                            bar = bar+"§7|";
                        }
                    }

                    message = message.replace("%player%", data.player.getName())
                            .replace("%vl%", ""+violations)
                            .replace("%vlbar%", bar)
                            .replace("%maxvl%", ""+banVL)
                            .replace("%check%", checkInfo.name().split(" ")[0])
                            .replace("%type%", checkInfo.name().split(" ")[1].replaceAll("[^A-Z]+", ""));

                    TextComponent alert = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
                    alert.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.
                            translateAlternateColorCodes('&',
                                    "&9Description: \n"+
                                            "&f"+checkInfo.description()+"\n\n"+
                                            "&9Debug: \n"+
                                            "&f"+debug+"\n\n"+
                                            "&9Click to teleport")).create()));
                    alert.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.player.getName()));

                    player.spigot().sendMessage(alert);
                }
            }
        });

        if(bannable && violations >= banVL && !checkInfo.experimental()) {
            if(CheatGuard.getInstance().config.getConfig().getBoolean("punishments.enabled")
                    && (!((CheatGuard.getInstance().config.getConfig().getString("settings.op-bypass").equalsIgnoreCase("full") || CheatGuard.getInstance().config.getConfig().getString("settings.op-bypass").equalsIgnoreCase("punish")) && data.player.isOp()) || !data.player.isOp())) {
                List<String> commands = CheatGuard.getInstance().config.getConfig().getStringList("punishments.commands");

                Bukkit.getScheduler().runTask(CheatGuard.getInstance(), () -> {
                    for(String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', command.replace("%player%", data.player.getName()).replace("%check%", checkInfo.name())));
                    }
                    for(Check check : data.getChecks()) {
                        check.violations = 0;
                    }
                });
            }
        }
    }

    protected void setback() {
        final String type = CheatGuard.getInstance().config.getConfig().getString("settings.setback");

        if(!type.equalsIgnoreCase("NONE")) {
            switch(type) {
                case "GROUND":
                    CustomLocation loc = data.movementProcessor.lastGroundLocation != null ? data.movementProcessor.lastGroundLocation : data.movementProcessor.location;
                    Bukkit.getScheduler().runTask(CheatGuard.getInstance(), () -> {
                        data.player.teleport(loc.toLocation(data.player));
                    });
                    break;
                default:
                    break;
            }
        }
    }

    protected long time() {
        return System.currentTimeMillis();
    }

    public void handle(WrappedPacket wrappedPacket) {}

}
