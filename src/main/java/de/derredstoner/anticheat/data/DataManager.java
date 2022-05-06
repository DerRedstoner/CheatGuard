package de.derredstoner.anticheat.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private List<PlayerData> datas = new ArrayList<>();

    public void createData(Player player) {
        if(getData(player) == null) {
            this.datas.add(new PlayerData(player));
        }
    }

    public void removeData(Player player) {
        final PlayerData playerData = getData(player);
        if(playerData != null) {
            this.datas.remove(playerData);
        }
    }

    public PlayerData getData(Player player) {
        return new ArrayList<>(datas).stream().filter(d -> d.player == player).findFirst().orElse(null);
    }

}
