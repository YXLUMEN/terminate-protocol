package lumen.terminate_protocol.network;

import lumen.terminate_protocol.network.packet.WeaponFireResultSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.clientDrawTrack;

public class ReceiveWeaponActions {
    private static int drawCooldown = 0;

    public static void register() {
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

                if (drawCooldown-- > 0) return;
                drawCooldown = 6;
                clientDrawTrack(world, start, end);
            });
        });
    }
}
