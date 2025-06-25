package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.item.weapon.IPullbolt;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.WeaponFireC2SPacket;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.*;
import static lumen.terminate_protocol.weapon_handler.ClientWeaponActionHandler.getIsPullbolt;
import static lumen.terminate_protocol.weapon_handler.ClientWeaponActionHandler.getWasAiming;

public class ClientFireHandler {
    public static final WeaponCooldownManager pullboltManager = new WeaponCooldownManager();
    private static boolean startFire = false;
    private static boolean canFire = false;

    private static long lastFireTime = 0;
    private static int lastFireCooldown = 0;

    private static float verticalOffset = 0;
    private static float horizontalOffset = 0;

    public static void doStartFire() {
        startFire = true;
    }

    public static void doFireTick(ClientPlayerEntity player, ItemStack stack, WeaponItem item) {
        canFire = false;

        long currentTime = System.currentTimeMillis();
        WeaponFireMode fireMode = item.getSettings().getFireMode();

        if (currentTime - lastFireTime < lastFireCooldown) {
            if (startFire && fireMode == WeaponFireMode.BOLT) doEndFire(fireMode);
            return;
        }

        if (stack.getDamage() >= stack.getMaxDamage()) return;
        if (getIsPullbolt()) return;

        canFire = true;
        lastFireTime = currentTime;
        lastFireCooldown = item.getSettings().getFireRate();

        ClientPlayNetworking.send(new WeaponFireC2SPacket());

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = getWasAiming() ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        // 后坐力控制
        Random random = player.getRandom();
        float recoilFactor = player.isSneaking() ? 0.7f : 1.0f;
        verticalOffset = item.getVerticalRecoil(random) * recoilFactor;
        horizontalOffset = item.getHorizontalRecoil(random) * recoilFactor;

        WeaponRecoilSystem.applyRecoil(verticalOffset, horizontalOffset);

        // 开火特效
        createBulletTrack(player, muzzlePos, fireMode == WeaponFireMode.BOLT);
        if (!getWasAiming()) spawnMuzzleFlash(player, muzzlePos, lookVec);

        // 音效处理
        ISoundRecord fireRecord = item.getStageSound(WeaponStage.FIRE, stack);
        if (fireRecord != null) clientPlaySoundRecord(fireRecord, player);

        if (fireMode == WeaponFireMode.FULL_AUTOMATIC) return;

        // 拉栓
        int pullboltTick = ((IPullbolt) item).getPullboltTick();
        pullboltManager.set(item, pullboltTick);

        if (!startFire) startFire = true;
        if (fireMode == WeaponFireMode.HALF_AUTOMATIC) doEndFire(fireMode);
    }

    public static void doEndFire(WeaponFireMode mode) {
        if (mode != WeaponFireMode.FULL_AUTOMATIC) {
            WeaponRecoilSystem.applyRecovery(verticalOffset, horizontalOffset);
        }

        startFire = false;

        verticalOffset = 0;
        horizontalOffset = 0;
    }

    public static boolean canFire() {
        return canFire;
    }

    private static void createBulletTrack(ClientPlayerEntity player, Vec3d startPos, boolean fullTrack) {
        World world = player.getWorld();
        Vec3d endPos = startPos.add(player.getRotationVec(1.0f).multiply(fullTrack ? 180 : 60));
        Vec3d randomOffset = getRandomDirection(player.getRandom(), 80, 20, 60);

        if (!fullTrack) {
            Vec3d velocity = endPos.subtract(startPos).multiply(0.8f).add(randomOffset);
            world.addParticle(ParticleTypes.END_ROD, startPos.x, startPos.y, startPos.z, velocity.x, velocity.y, velocity.z);
            return;
        }

        BlockHitResult blockHit = world.raycast(new RaycastContext(
                startPos, endPos.add(randomOffset),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        clientDrawTrack(world, startPos, blockHit.getPos());
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

    public static void clientPlaySoundRecord(ISoundRecord record, ClientPlayerEntity player) {
        switch (record) {
            case SoundHelper.SingleSound(SoundEvent sound, float volume, float pitch) ->
                    player.playSound(sound, volume, pitch);
            case SoundHelper.MultiSound(java.util.List<SoundHelper.SingleSound> sounds) ->
                    sounds.forEach(singleSound -> player.playSound(singleSound.sound(), singleSound.volume(), singleSound.pitch()));
            case null, default -> {
            }
        }

    }
}
