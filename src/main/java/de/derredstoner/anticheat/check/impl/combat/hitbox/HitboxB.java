package de.derredstoner.anticheat.check.impl.combat.hitbox;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(
        name = "Hitbox (B)",
        description = "Checks for client missing target hitbox",
        category = Category.COMBAT,
        subCategory = SubCategory.HITBOX
)
public class HitboxB extends Check {

    public HitboxB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {

    }

}
