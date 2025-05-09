package lumen.terminate_protocol.network;

import lumen.terminate_protocol.item.AbstractGunItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class ServerFireHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(GunFireC2SPayload.ID, (payload, context) -> {
            long fireTime = payload.fireTime();

            context.player().server.execute(() -> {
                PlayerEntity player = context.player();
                Item item = player.getMainHandStack().getItem();

                if (!(item instanceof AbstractGunItem firearmsItem)) return;
                long currentTime = System.currentTimeMillis();
                if (fireTime <= 0 || fireTime > currentTime || currentTime - fireTime > firearmsItem.getFireRant() * 50L) {
                    return;
                }

                firearmsItem.onFire(player.getWorld(), player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(GunReloadC2SPayload.ID, (payload, context) -> context.player().server.execute(() -> {
            PlayerEntity player = context.player();
            Item item = player.getMainHandStack().getItem();

            if (item instanceof AbstractGunItem firearmsItem) {
                firearmsItem.onReload(player.getWorld(), player);
            }
        }));
    }
}
