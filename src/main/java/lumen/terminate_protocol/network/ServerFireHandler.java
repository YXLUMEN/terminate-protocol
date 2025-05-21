package lumen.terminate_protocol.network;

import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.GunFireC2SPacket;
import lumen.terminate_protocol.network.packet.GunReloadC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerFireHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GunFireC2SPacket.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof WeaponItem item) {
                item.onFire(player.getWorld(), player, stack);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(GunReloadC2SPacket.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof WeaponItem item) {
                item.onReload(player.getWorld(), player, stack);
            }
        }));
    }
}
