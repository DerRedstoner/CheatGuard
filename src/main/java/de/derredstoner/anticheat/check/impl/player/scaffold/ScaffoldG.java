package de.derredstoner.anticheat.check.impl.player.scaffold;

import com.comphenix.protocol.wrappers.BlockPosition;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockPlace;

@CheckInfo(
        name = "Scaffold (G)",
        description = "Checks for invalid place direction",
        category = Category.PLAYER,
        subCategory = SubCategory.SCAFFOLD
)
public class ScaffoldG extends Check {

    public ScaffoldG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInBlockPlace) {
            WrappedPacketPlayInBlockPlace wrapper = (WrappedPacketPlayInBlockPlace) wrappedPacket;

            BlockPosition blockPosition = wrapper.getBlockPosition();
            int face = wrapper.getFace();

            if(face == 0 && data.player.getLocation().getY() > blockPosition.getY()) {
                flag("locY="+data.player.getLocation().getY()+"\nblockY="+blockPosition.getY());
            }
        }
    }

}
