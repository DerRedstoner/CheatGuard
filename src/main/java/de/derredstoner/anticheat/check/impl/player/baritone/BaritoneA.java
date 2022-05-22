package de.derredstoner.anticheat.check.impl.player.baritone;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.check.Check;
import de.derredstoner.anticheat.check.annotation.CheckInfo;
import de.derredstoner.anticheat.check.categories.Category;
import de.derredstoner.anticheat.check.categories.SubCategory;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.WrappedPacket;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInBlockDig;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

@CheckInfo(
        name = "Baritone (A)",
        description = "Checks for baritone like rotations",
        category = Category.PLAYER,
        subCategory = SubCategory.BARITONE,
        experimental = true
)
public class BaritoneA extends Check {

    public BaritoneA(PlayerData data) {
        super(data);
    }

    private double lastDivisor;
    private long lastRotation;
    private boolean checking, waitForBreak;

    @Override
    public void handle(WrappedPacket wrappedPacket) {
        if(wrappedPacket instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) wrappedPacket;

            if(data.movementProcessor.teleporting) {
                return;
            }

            if(wrapper.isRotating()) {
                final float deltaPitch = Math.abs(data.movementProcessor.deltaPitch);
                final float lastDeltaPitch = Math.abs(data.movementProcessor.lastDeltaPitch);

                if(deltaPitch > 0.1 && lastDeltaPitch > 0.1) {
                    final long expanded = (long) (deltaPitch * MathUtil.EXPANDER);
                    final long lastExpanded = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                    final long gcd = MathUtil.gcd(expanded, lastExpanded);

                    lastDivisor = gcd / MathUtil.EXPANDER;
                    checking = true;
                }

                lastRotation = time();
            }
        } else if(wrappedPacket instanceof WrappedPacketPlayInBlockDig) {
            WrappedPacketPlayInBlockDig wrapper = (WrappedPacketPlayInBlockDig) wrappedPacket;

            if(data.player.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            }

            if(wrapper.getPlayerDigType() == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                waitForBreak = false;

                if(checking) {
                    if((lastDivisor > 0 && lastDivisor < 0.009) || lastDivisor > 10) {
                        waitForBreak = true;
                    } else buffer = Math.max(0, buffer - 2);
                }

                checking = false;
            } else if(wrapper.getPlayerDigType() == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                if(waitForBreak) {
                    if(buffer++ > 10) {
                        flag("divisor="+lastDivisor);
                    }
                }

                waitForBreak = false;
            }
        }
    }

}
