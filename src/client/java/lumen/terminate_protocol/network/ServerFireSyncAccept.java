package lumen.terminate_protocol.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class ServerFireSyncAccept {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(GunFireSyncS2CPayload.ID, (payload, context) -> {
            Vec3d start = payload.start();
            Vec3d end = payload.end();

            context.client().execute(() -> {
                ClientWorld world = context.player().clientWorld;

                Vec3d direction = end.subtract(start);
                double length = direction.length();
                direction = direction.normalize();

                for (int i = 0; i <= length; i++) {
                    Vec3d pos = start.add(direction.multiply(i));
                    world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
                }
            });
        });
    }
}
