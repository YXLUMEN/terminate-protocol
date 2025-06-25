package lumen.terminate_protocol.util.weapon;

import lumen.terminate_protocol.network.packet.WeaponFireResultSyncS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WeaponHelper {
    public static void syncTrack(Entity attacker, Vec3d start, Vec3d end, boolean important) {
        if (!attacker.isPlayer()) return;
        var payload = new WeaponFireResultSyncS2CPacket(start, end, important);
        for (ServerPlayerEntity player : PlayerLookup.tracking(attacker)) {
            if (player == null || player.equals(attacker)) continue;
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void clientDrawTrack(World world, Vec3d start, Vec3d end) {
        if (!world.isClient) return;
        Random random = world.getRandom();

        Vec3d direction = end.subtract(start);
        double length = direction.length();
        direction = direction.normalize();

        for (int i = 0; i <= length; i++) {
            Vec3d pos = start.add(direction.multiply(i));
            world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z,
                    (random.nextFloat() - 0.5) * 0.03,
                    (random.nextFloat() - 0.5) * 0.03,
                    (random.nextFloat() - 0.5) * 0.03
            );
        }
    }

    public static void clientDrawBullet(World world, Vec3d start, Vec3d end) {
        if (!world.isClient) return;

        Vec3d velocity = end.subtract(start).normalize().multiply(60);
        world.addParticle(ParticleTypes.END_ROD, start.x, start.y, start.z, velocity.x, velocity.y, velocity.z);
    }

    public static void debugDrawRay(World world, Vec3d start, Vec3d end, SimpleParticleType particleType, int amplifier) {
        if (world.isClient) return;

        Vec3d direction = end.subtract(start);
        double length = direction.length();
        direction = direction.normalize();

        int particleCount = (int) (length * amplifier);
        if (particleCount <= 0) return;

        double step = length / particleCount;

        for (int i = 0; i <= particleCount; i++) {
            Vec3d pos = start.add(direction.multiply(i * step));
            ((ServerWorld) world).spawnParticles(
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

        return new Vec3d(Math.cos(theta) * radius, y, Math.sin(theta) * radius).normalize();
    }

    public static Vec3d getRandomDirection(Random random, float maxPitchAngle, float minYaw, float maxYaw) {
        // 限制 Y 分量（垂直角度）
        double minY = Math.cos(Math.toRadians(maxPitchAngle));
        double y = minY + (1 - minY) * random.nextDouble();
        y = Math.min(Math.max(y, -1), 1);

        // 计算水平半径
        double radius = Math.sqrt(1 - y * y);

        // 限制水平角度
        double theta = Math.toRadians(minYaw + random.nextDouble() * (maxYaw - minYaw));

        return new Vec3d(Math.cos(theta) * radius, y, Math.sin(theta) * radius).normalize();
    }

    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
        float g = -MathHelper.sin(pitch * (float) (Math.PI / 180.0));
        float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));

        return new Vec3d(f, g, h).normalize();
    }

    public static int findItemSlot(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) return i;
        }
        return -1;
    }

    public static Vec3d getMuzzleOffset(PlayerEntity player, Vec3d lookVec) {
        Vec3d rightOffset = new Vec3d(-lookVec.z, 0, lookVec.x).normalize().multiply(0.3);
        return player.getEyePos().add(rightOffset).add(0, -0.3, 0);
    }
}
