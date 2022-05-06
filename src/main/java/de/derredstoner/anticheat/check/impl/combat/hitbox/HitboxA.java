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
        name = "Hitbox (A)",
        description = "Checks for expanded enemy hitbox",
        category = Category.COMBAT,
        subCategory = SubCategory.HITBOX
)
public class HitboxA extends Check {

    public HitboxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            Vector hitVec = wrapper.getHitVec();

            if(hitVec != null && data.actionProcessor.lastTarget instanceof Player) {
                if(Math.abs(hitVec.getX()) > 0.4000000059604645 || Math.abs(hitVec.getY()) > 1.91 || Math.abs(hitVec.getZ()) > 0.4000000059604645) {
                    flag("x="+hitVec.getX()+"\ny="+hitVec.getY()+"\nz="+hitVec.getZ());
                }
            }
        }
    }

}
