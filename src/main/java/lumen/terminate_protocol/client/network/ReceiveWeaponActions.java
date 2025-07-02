package lumen.terminate_protocol.client.network;

import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.WeaponCooldownUpdateS2CPacket;
import lumen.terminate_protocol.network.packet.WeaponFireResultSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.clientDrawBullet;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.clientDrawTrack;

public class ReceiveWeaponActions {
    private static int cooldown = 0;

    public static void register() {
        // 绘制弹道
        ClientPlayNetworking.registerGlobalReceiver(WeaponFireResultSyncS2CPacket.ID, (payload, context) -> {
            Vec3d start = payload.start();
            Vec3d end = payload.end();
            boolean important = payload.important();

            context.client().execute(() -> {
                ClientWorld world = context.player().clientWorld;

                if (important) {
                    clientDrawTrack(world, start, end);
                    return;
                }

                if (cooldown++ < 3) return;
                cooldown = 0;
                clientDrawBullet(world, start, end);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(WeaponCooldownUpdateS2CPacket.ID, (payload, context) -> {
            int duration = payload.duration();
            Item item = payload.item();

            var player = context.player();
            if (player instanceof WeaponAccessor weaponAccessor) {
                var manager = weaponAccessor.terminate_protocol$getWpnCooldownManager();
                if (item instanceof WeaponItem wItem) {
                    manager.set(wItem, duration);
                }
            }
        });
    }
}
