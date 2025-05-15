package lumen.terminate_protocol.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import static lumen.terminate_protocol.util.RayCasterTools.drawTrack;

public class ServerFireSyncAccept {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(GunFireSyncS2CPayload.ID, (payload, context) -> {
            Vec3d start = payload.start();
            Vec3d end = payload.end();

            context.client().execute(() -> {
                ClientWorld world = context.player().clientWorld;
                drawTrack(world, start, end);
            });
        });
    }
}
