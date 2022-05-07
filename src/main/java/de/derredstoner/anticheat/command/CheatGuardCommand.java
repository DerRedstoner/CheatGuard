package de.derredstoner.anticheat.command;

import de.derredstoner.anticheat.CheatGuard;
import de.derredstoner.anticheat.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheatGuardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            PlayerData data = CheatGuard.getInstance().getDataManager().getData((Player) sender);

            if(data != null) {
                if(data.player.hasPermission("cheatguard.admin") || data.player.isOp()) {
                    if(args.length == 2 && args[0].equalsIgnoreCase("info")) {
                        Player targetPlayer = Bukkit.getPlayer(args[1]);

                        if(targetPlayer != null) {
                            PlayerData targetData = CheatGuard.getInstance().getDataManager().getData(targetPlayer);

                            data.player.sendMessage(
                                    "§f[§9CheatGuard§f] §bShowing info for §f"+targetPlayer.getName()+"§7:\n"+
                                            "§bGamemode§7: §f"+targetData.player.getGameMode()+"\n"+
                                            "§bPing§7: §f"+targetData.connectionProcessor.transactionPing+" ms\n"+
                                            "§bSensitivity§7: §f"+targetData.sensitivityProcessor.sensitivityPercentage+" %"
                            );
                        }
                    }
                    else if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                        CheatGuard.getInstance().config.reloadConfig();
                        data.player.sendMessage("§f[§9CheatGuard§f] §aReloaded config");
                    }
                    else if(args.length == 1 && args[0].equalsIgnoreCase("alerts")) {
                        if(data.alerts) {
                            data.player.sendMessage("§f[§9CheatGuard§f] §cToggled alerts off");
                        } else {
                            data.player.sendMessage("§f[§9CheatGuard§f] §aToggled alerts on");
                        }

                        data.alerts = !data.alerts;
                    }
                    else {
                        data.player.sendMessage(
                                "§f[§9CheatGuard§f] §bVersion§7: §f"+CheatGuard.getInstance().getDescription().getVersion()+"\n"+
                                        "§b/cheatguard §7- §fShow this help page\n"+
                                        "§b/cheatguard alerts §7- §fToggle alert messages\n"+
                                        "§b/cheatguard info <player> §7- §fShow info about player\n"+
                                        "§b/cheatguard reload §7- §fReload the configuration\n"
                        );
                    }
                } else {
                    data.player.sendMessage("§cNo permission!");
                }
            }
        } else {
            sender.sendMessage("This command can not be executed through console!");
        }

        return false;
    }

}
