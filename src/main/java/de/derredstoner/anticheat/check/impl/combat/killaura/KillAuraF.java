package de.derredstoner.anticheat.check.impl.combat.killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInUseEntity;
import de.derredstoner.anticheat.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "KillAura (F)",
        description = "Checks for keepsprint",
        category = Category.COMBAT,
        subCategory = SubCategory.KILLAURA
)
public class KillAuraF extends Check {

    public KillAuraF(PlayerData data) {
        super(data);
    }

    private int hitTicks;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            double deltaXZ = data.movementProcessor.deltaXZ;
            double lastDeltaXZ = data.movementProcessor.lastDeltaXZ;

            double accelXZ = deltaXZ - lastDeltaXZ;

            if(!data.movementProcessor.clientGround) {
                buffer = Math.max(0, buffer - 1);
            }

            if(hitTicks++ < 3) {
                if(accelXZ < -0.002) {
                    buffer = 0;
                } else {
                    if(buffer++ > 3) {
                        flag("deltaXZ="+deltaXZ+"\naccelXZ="+accelXZ);
                    }
                }
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) wrappedPacket;

            if(wrapper.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                Entity entity = data.player.getWorld().getEntities().stream().filter(en -> en.getEntityId() == wrapper.getEntityId()).findFirst().orElse(null);

                double deltaXZ = data.movementProcessor.deltaXZ;
                double sprintSpeed = 0.2203 + (data.player.getWalkSpeed() - 0.2F) * 1.6F + 0.0441 * PlayerUtil.getAmplifier(data.player, PotionEffectType.SPEED);

                if(entity != null && entity instanceof Player && (data.actionProcessor.sprinting || deltaXZ > sprintSpeed) && data.movementProcessor.clientGroundTicks > 2) {
                    hitTicks = 0;
                }
            }
        }
    }

}
