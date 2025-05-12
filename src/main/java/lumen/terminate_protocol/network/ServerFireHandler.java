package lumen.terminate_protocol.network;

import lumen.terminate_protocol.item.guns.AbstractGunItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerFireHandler {
    private static final int MAX_TOLERANT_TICK = 10;

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GunFireC2SPayload.ID, (payload, context) -> {
            long clientTime = payload.clientTime();

            context.player().server.execute(() -> {
                ServerPlayerEntity player = context.player();
                ItemStack item = player.getMainHandStack();

                if (!(item.getItem() instanceof AbstractGunItem firearmsItem)) return;

                long currentTick = player.getWorld().getTime();
                if (clientTime <= 0 || clientTime > currentTick || currentTick - clientTime > MAX_TOLERANT_TICK) {
                    return;
                }

                firearmsItem.onFire(player.getWorld(), player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(GunReloadC2SPayload.ID, (payload, context) -> context.player().server.execute(() -> {
            ServerPlayerEntity player = context.player();
            Item item = player.getMainHandStack().getItem();

            if (item instanceof AbstractGunItem firearmsItem) {
                firearmsItem.onReload(player.getWorld(), player);
            }
        }));
    }
}
