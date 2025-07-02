package lumen.terminate_protocol.client.weapon_handler;

import lumen.terminate_protocol.api.WeaponAccessor;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static lumen.terminate_protocol.client.weapon_handler.ClientWeaponActionHandler.getIsPullbolt;
import static lumen.terminate_protocol.client.weapon_handler.ClientWeaponActionHandler.getWasAiming;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.getMuzzleOffset;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.getPlayerLookVec;

public class ClientFireHandler {
    public static final WeaponCooldownManager reloadManager = new WeaponCooldownManager();

    private static boolean startFire = false;

    private static float verticalOffset = 0;
    private static float horizontalOffset = 0;

    public static void doStartFire() {
        startFire = true;
    }

    public static void doFireTick(ClientPlayerEntity player, ItemStack stack, WeaponItem item) {
        WeaponFireMode fireMode = item.getSettings().getFireMode();
        WeaponCooldownManager fireRateManager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (fireRateManager.isCoolingDown(item)) {
            if (startFire && fireMode == WeaponFireMode.BOLT) doEndFire(fireMode);
            return;
        }

        if (stack.getDamage() >= stack.getMaxDamage()) return;
        if (getIsPullbolt()) return;

        fireRateManager.set(item, item.getSettings().getFireRate());

        ClientPlayNetworking.send(new WeaponFireC2SPacket());

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = getWasAiming() ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        // 后坐力控制
        Random random = player.getRandom();
        float recoilFactor = player.isSneaking() ? 0.7f : 1.0f;
        verticalOffset = item.getVerticalRecoil(random) * recoilFactor;
        horizontalOffset = item.getHorizontalRecoil(random) * recoilFactor;

        ClientWeaponRecoilSystem.applyRecoil(verticalOffset, horizontalOffset);

        // 开火特效
        if (!getWasAiming()) spawnMuzzleFlash(player, muzzlePos, lookVec);

        // 音效处理
        ISoundRecord fireRecord = item.getStageSound(WeaponStage.FIRE, stack);
        if (fireRecord != null) clientPlaySoundRecord(fireRecord, player);

        if (fireMode == WeaponFireMode.FULL_AUTOMATIC) return;

        // 拉栓
        int pullboltTick = ((IPullbolt) item).getPullboltTick();
        reloadManager.set(item, pullboltTick);

        if (!startFire) startFire = true;
        if (fireMode == WeaponFireMode.HALF_AUTOMATIC) doEndFire(fireMode);
    }

    public static void doEndFire(WeaponFireMode mode) {
        if (mode != WeaponFireMode.FULL_AUTOMATIC) {
            ClientWeaponRecoilSystem.applyRecovery(verticalOffset, horizontalOffset);
        }

        startFire = false;

        verticalOffset = 0;
        horizontalOffset = 0;
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
