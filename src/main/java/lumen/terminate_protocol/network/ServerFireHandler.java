package lumen.terminate_protocol.network;

import lumen.terminate_protocol.item.guns.AbstractWeaponItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerFireHandler {
    private static final int MAX_TOLERANT_MS = 600;

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GunFireC2SPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                ServerPlayerEntity player = context.player();
                ItemStack stack = player.getMainHandStack();

                if (!(stack.getItem() instanceof AbstractWeaponItem firearmsItem)) return;

                firearmsItem.onFire(player.getWorld(), player, stack);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(GunReloadC2SPayload.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof AbstractWeaponItem firearmsItem) {
                firearmsItem.onReload(player.getWorld(), player, stack);
            }
        }));
    }
}
