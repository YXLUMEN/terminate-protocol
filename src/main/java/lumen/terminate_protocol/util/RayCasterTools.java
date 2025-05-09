package lumen.terminate_protocol.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

// debug
public class RayCasterTools {
    public static void debugDrawRay(World world, Vec3d start, Vec3d end, SimpleParticleType particleType, int amplifier) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        Vec3d direction = end.subtract(start);
        double length = direction.length();
        direction = direction.normalize();

        int particleCount = (int) (length * amplifier);
        if (particleCount <= 0) return;

        double step = length / particleCount;

        for (int i = 0; i <= particleCount; i++) {
            Vec3d pos = start.add(direction.multiply(i * step));
            serverWorld.spawnParticles(
                    particleType,
                    pos.x, pos.y, pos.z,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static Vec3d getRandomDirection(Random random) {
        double y = 1 - (random.nextDouble() * 2);
        double radius = Math.sqrt(1 - y * y);
        double theta = random.nextDouble() * (2 * Math.PI);

        return new Vec3d(
                Math.cos(theta) * radius,
                y,
                Math.sin(theta) * radius
        ).normalize();
    }

    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
        float g = -MathHelper.sin(pitch * (float) (Math.PI / 180.0));
        float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));

        return new Vec3d(f, g, h).normalize();
    }
}
