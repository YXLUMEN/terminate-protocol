package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.item.guns.AbstractWeaponItem;
import lumen.terminate_protocol.network.GunFireC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import static lumen.terminate_protocol.item.guns.AbstractWeaponItem.getMuzzleOffset;
import static lumen.terminate_protocol.util.RayCasterTools.drawTrack;
import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public class ClientFireHandler {
    private static boolean startFire = false;

    private static long lastFireTime = 0;
    private static int lastFireCooldown = 0;

    private static float verticalOffset = 0;
    private static float horizontalOffset = 0;

    public static void onStartFire() {
        startFire = true;
    }

    public static void onFireTick(ClientPlayerEntity player, ItemStack itemStack, boolean isAiming) {
        if (itemStack.getDamage() >= itemStack.getMaxDamage()) return;

        long currentTime = System.currentTimeMillis();
        AbstractWeaponItem item = (AbstractWeaponItem) itemStack.getItem();

        if (currentTime - lastFireTime < lastFireCooldown) {
            if (startFire && item.getRecoilType() == 1) onEndFire(item);
            return;
        }

        lastFireTime = currentTime;
        lastFireCooldown = item.getFireRant();

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = isAiming ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        createBulletTrack(player, muzzlePos, item.getRecoilType() == 1);
        if (!isAiming) spawnMuzzleFlash(player, muzzlePos, lookVec);

        // 后坐力控制
        Random random = player.getRandom();
        float recoilFactor = player.isSneaking() ? 0.7f : 1.0f;
        verticalOffset = item.getVerticalRecoil(random) * recoilFactor;
        horizontalOffset = item.getHorizontalRecoil(random) * recoilFactor;

        WeaponRecoilSystem.applyRecoil(verticalOffset, horizontalOffset);

        GunFireC2SPayload payload = new GunFireC2SPayload(currentTime);
        ClientPlayNetworking.send(payload);

        if (!startFire) onStartFire();
    }

    public static void onEndFire(AbstractWeaponItem item) {
        if (item.getRecoilType() == 1) {
            WeaponRecoilSystem.applyRecovery(verticalOffset, horizontalOffset);
        }

        startFire = false;

        verticalOffset = 0;
        horizontalOffset = 0;
    }

    private static void createBulletTrack(ClientPlayerEntity player, Vec3d startPos, boolean fullTrack) {
        World world = player.getWorld();

        Vec3d endPos = startPos.add(player.getRotationVec(1.0f).multiply(fullTrack ? 160 : 64));

        if (!fullTrack) {
            Vec3d velocity = endPos.subtract(startPos).multiply(0.8f);
            world.addParticle(ParticleTypes.END_ROD, startPos.x, startPos.y, startPos.z, velocity.x, velocity.y, velocity.z);
            return;
        }

        BlockHitResult blockHit = world.raycast(new RaycastContext(
                startPos, endPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        drawTrack(world, startPos, blockHit.getPos());
    }

    private static void spawnMuzzleFlash(ClientPlayerEntity player, Vec3d muzzlePos, Vec3d direction) {
        World world = player.getWorld();
        Random random = player.getRandom();

        world.addParticle(ParticleTypes.SMOKE,
                muzzlePos.x, muzzlePos.y, muzzlePos.z,
                direction.x * 0.1,
                0.02,
                direction.z * 0.1);


        if (random.nextFloat() < 0.5f) {
            world.addParticle(ParticleTypes.FLAME,
                    muzzlePos.x, muzzlePos.y, muzzlePos.z,
                    direction.x * 0.2 + random.nextGaussian() * 0.05,
                    0.05 + random.nextDouble() * 0.1,
                    direction.z * 0.2 + random.nextGaussian() * 0.05);
        }
    }
}
