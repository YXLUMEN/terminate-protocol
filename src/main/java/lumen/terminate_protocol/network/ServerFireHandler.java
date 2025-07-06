package lumen.terminate_protocol.network;

import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.WeaponAimC2SPacket;
import lumen.terminate_protocol.network.packet.WeaponFireC2SPacket;
import lumen.terminate_protocol.network.packet.WeaponReloadC2SPacket;
import lumen.terminate_protocol.network.packet.WeaponStartFireC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerFireHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(WeaponFireC2SPacket.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof WeaponItem item) {
                item.onFire(player.getWorld(), player, stack);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(WeaponReloadC2SPacket.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof WeaponItem item) {
                item.onReload(player.getWorld(), player, stack);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(WeaponAimC2SPacket.ID, (payload, context) -> {
            boolean aiming = payload.aiming();
            context.player().server.execute(() -> ((WeaponAccessor) context.player()).terminate_protocol$setWpnAiming(aiming));
        });

        ServerPlayNetworking.registerGlobalReceiver(WeaponStartFireC2SPacket.ID, ((payload, context) -> {
            boolean start = payload.start();
            context.player().server.execute(() -> {
                ServerPlayerEntity player = context.player();
                ItemStack stack = player.getMainHandStack();

                if (stack.getItem() instanceof WeaponItem item) {
                    if (start) {
                        item.onStartFire(player.getWorld(), player, stack);
                    } else {
                        item.onStopFire(player.getWorld(), player, stack);
                    }
                }
            });
        }));
    }
}
