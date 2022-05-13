package de.derredstoner.anticheat.check.impl.player.scaffold;

import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;

@CheckInfo(
        name = "Scaffold (D)",
        description = "Checks for invalid place vector",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldD extends Check {

    public ScaffoldD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            float x = wrapper.getFacingX();
            float y = wrapper.getFacingY();
            float z = wrapper.getFacingZ();
            int face = wrapper.getFace();

            if(face != 255 && (x > 1.001 || y > 1.001 || z > 1.001)) {
                flag("x="+x+"\ny="+y+"\nz="+z);
            }
        }
    }

}
