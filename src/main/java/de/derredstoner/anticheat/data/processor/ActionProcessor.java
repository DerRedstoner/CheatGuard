package de.derredstoner.anticheat.data.processor;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.*;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutCloseWindow;
import de.derredstoner.anticheat.packet.wrapper.server.WrappedPacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class ActionProcessor {

    private final PlayerData data;

    public Entity lastTarget;
    public boolean sprinting, sneaking, inventoryOpen, isDigging, pendingInventoryOpen, elytraFlying;
    public long vehicleTicks, lastSprintToggle, lastHit, lastBlockPlace;
    public short transactionId;

    public ActionProcessor(PlayerData data) {
        this.data = data;
    }

    public void process(Object e) {
        if(e instanceof WrappedPacketPlayInEntityAction) {
            WrappedPacketPlayInEntityAction wrapper = (WrappedPacketPlayInEntityAction) e;

            if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SPRINTING) {
                sprinting = true;
                lastSprintToggle = System.currentTimeMillis();
            } else if(wrapper.getAction() == EnumWrappers.PlayerAction.STOP_SPRINTING) {
                sprinting = false;
                lastSprintToggle = System.currentTimeMillis();
            } else if(wrapper.getAction() == EnumWrappers.PlayerAction.START_SNEAKING) {
                sneaking = true;
            } else if(wrapper.getAction() == EnumWrappers.PlayerAction.STOP_SNEAKING) {
                sneaking = false;
            } else if(wrapper.getAction() == EnumWrappers.PlayerAction.START_FALL_FLYING) {
                elytraFlying = true;
            }
        } else if(e instanceof WrappedPacketPlayInUseEntity) {
            WrappedPacketPlayInUseEntity wrapper = (WrappedPacketPlayInUseEntity) e;

            if(wrapper.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
                lastHit = System.currentTimeMillis();
                if(wrapper.getEntityId() != 0) {
                    lastTarget = data.player.getWorld().getEntities().stream().filter(entity -> entity.getEntityId() == wrapper.getEntityId()).findFirst().orElse(null);
                }
            }

            isDigging = false;
        } else if(e instanceof WrappedPacketPlayInBlockDig) {
            WrappedPacketPlayInBlockDig wrapper = (WrappedPacketPlayInBlockDig) e;

            if(wrapper.getPlayerDigType() == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                isDigging = true;
            }
        } else if(e instanceof WrappedPacketPlayInTransaction) {
            WrappedPacketPlayInTransaction wrapper = (WrappedPacketPlayInTransaction) e;

            if(pendingInventoryOpen && wrapper.getTransactionId() == transactionId) {
                inventoryOpen = true;
                pendingInventoryOpen = false;
            }

            if(data.player.getVehicle() != null) {
                vehicleTicks = 0;
            } else {
                vehicleTicks++;
            }
        } else if(e instanceof WrappedPacketPlayInClientCommand) {
            WrappedPacketPlayInClientCommand wrapper = (WrappedPacketPlayInClientCommand) e;

            if(wrapper.getClientCommand() == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                inventoryOpen = true;
            }
        } else if(e instanceof WrappedPacketPlayInCloseWindow) {
            inventoryOpen = false;
        } else if(e instanceof WrappedPacketPlayOutCloseWindow) {
            inventoryOpen = false;
            pendingInventoryOpen = false;
        } else if(e instanceof WrappedPacketPlayInPosition) {
            sprinting = false;
            sneaking = false;
            inventoryOpen = false;
        } else if(e instanceof WrappedPacketPlayOutOpenWindow) {
            transactionId = (short) (data.connectionProcessor.transactionID + 1);
            pendingInventoryOpen = true;
        } else if(e instanceof WrappedPacketPlayInFlying) {
            if(elytraFlying && data.movementProcessor.deltaY == 0 && data.movementProcessor.clientGround && data.movementProcessor.mathGround) {
                elytraFlying = false;
            }
        }
    }

}
