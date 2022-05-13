package de.derredstoner.anticheat.check.impl.combat.killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInArmAnimation;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;

@CheckInfo(
        name = "KillAura (E)",
        description = "Checks for noswing",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraE extends Check {

    public KillAuraE(PlayerData data) {
        super(data);
    }

    private int hits, swings;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInArmAnimation) {
            swings++;
        } else if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                if(hits++ > 3) {
                    if(swings < hits) {
                        flag("swings="+swings+"\nhits="+hits);
                    }

                    swings = hits = 0;
                }
            }
        }
    }

}
