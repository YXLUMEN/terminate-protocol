package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.api.TPDamageTypes;
import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getMuzzleOffset;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.getPlayerLookVec;

public class LStarItem extends WeaponItem {
    public LStarItem(Settings settings) {
        super(settings.maxDamage(100),
                new WeaponSettings(1, 70, null, WeaponFireMode.FULL_AUTOMATIC)
                        .setAimOffset(new Vec3d(-0.513f, 0.05f, 0))
                        .setAimFovMultiplier(0.7f)
                        .setRecoilDecayMultiplier(0.4f),
                new TrajectoryRayCaster()
                        .showTrack(true)
                        .baseDamage(3.5f)
                        .baseRayLength(100)
                        .penetrateChance(0)
                        .bounceChance(0)
                        .entityDetectRadius(1.5f)
                        .setDamageType(TPDamageTypes.ENERGY_BULLET_HIT));

        settings.component(TPComponentTypes.WPN_FIRING, false);
        settings.component(TPComponentTypes.WPN_OVERHEAT, false);
        settings.component(TPComponentTypes.WPN_COOLDOWN, 0);
    }

    @Override
    public void onStartFire(World world, PlayerEntity player, ItemStack stack) {
        if (stack.getDamage() >= stack.getMaxDamage() || stack.getOrDefault(TPComponentTypes.WPN_COOLDOWN, 0) > 0)
            return;
        stack.set(TPComponentTypes.WPN_FIRING, true);
    }

    @Override
    public void onFire(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return;

        int currentAmmo = stack.getDamage();
        if (currentAmmo >= stack.getMaxDamage() || stack.getOrDefault(TPComponentTypes.WPN_COOLDOWN, 0) > 0) return;

        WeaponCooldownManager manager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (manager.isCoolingDown(this)) return;

        stack.setDamage(currentAmmo + 1);
        stack.set(TPComponentTypes.WPN_FIRING, true);

        if (stack.getDamage() >= stack.getMaxDamage()) {
            this.onStopFire(world, player, stack);

            stack.set(TPComponentTypes.WPN_OVERHEAT, true);
            world.playSound(null, player.getBlockPos(), TPSoundEvents.LSTAR_OVERHEAT_ALARM, SoundCategory.PLAYERS);
        }

        boolean isAiming = ((WeaponAccessor) player).terminate_protocol$getWpnAiming();
        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = isAiming ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        this.getCaster().start(world, player, muzzlePos, lookVec);
        manager.set(this, Math.max(0, this.getSettings().getFireRate()));

        ISoundRecord record = this.getStageSound(WeaponStage.FIRE, stack);
        if (record == null) return;

        playSoundRecord(record, world, player, player.getPos());
    }

    @Override
    public void onStopFire(World world, PlayerEntity player, ItemStack stack) {
        stack.set(TPComponentTypes.WPN_FIRING, false);

        if (stack.getOrDefault(TPComponentTypes.WPN_COOLDOWN, 0) <= 0) {
            stack.set(TPComponentTypes.WPN_COOLDOWN, 24);
            world.playSound(null, player.getBlockPos(), TPSoundEvents.LSTAR_COOLDOWN, SoundCategory.PLAYERS);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!world.isClient && entity.isPlayer()) {
            // 停止射击开始冷却
            // 过热失效
            if (stack.getOrDefault(TPComponentTypes.WPN_FIRING, false) || stack.getOrDefault(TPComponentTypes.WPN_OVERHEAT, true)) {
                return;
            }

            // 冷却
            int cooldown = stack.getOrDefault(TPComponentTypes.WPN_COOLDOWN, 0);
            if (cooldown > 0) {
                stack.set(TPComponentTypes.WPN_COOLDOWN, cooldown - 1);
                return;
            }

            // 恢复弹药
            int heat = stack.getDamage();
            if (heat > 0) {
                stack.setDamage(heat - 10);
            }
        }
    }

    @Override
    public void onReload(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient || !stack.getOrDefault(TPComponentTypes.WPN_OVERHEAT, false)) return;
        WeaponCooldownManager manager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (manager.isCoolingDown(this)) return;

        manager.set(this, this.getSettings().getReloadTick());

        stack.set(TPComponentTypes.WPN_FIRING, false);
        stack.set(TPComponentTypes.WPN_COOLDOWN, 0);
        stack.set(TPComponentTypes.WPN_RELOADING, true);
    }

    @Override
    protected void restoreAmmo(ItemStack stack, PlayerEntity player) {
        stack.set(TPComponentTypes.WPN_RELOADING, false);
        stack.set(TPComponentTypes.WPN_OVERHEAT, false);
        stack.setDamage(0);

        ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager().set(this, 0);
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.2f + random.nextFloat() * 0.2f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.6f * (random.nextFloat() - 0.5f);
    }

    private static final Map<WeaponStage, ISoundRecord> sounds = Map.of(
            WeaponStage.FIRE, SoundHelper.of(TPSoundEvents.LSTAR_FIRE),
            WeaponStage.FIRSTSHOT, SoundHelper.of(TPSoundEvents.LSTAR_FIRSTSHOT),
            WeaponStage.MAGOUT, SoundHelper.of(TPSoundEvents.LSTAR_MAGOUT),
            WeaponStage.MAGIN, SoundHelper.of(TPSoundEvents.LSTAR_MAGIN),
            WeaponStage.BOLTBACK, SoundHelper.of(TPSoundEvents.LSTAR_BOLTBACK),
            WeaponStage.BOLTFORWARD, SoundHelper.of(TPSoundEvents.LSTAR_BOLTFORWARD)
    );

    @Override
    public @Nullable ISoundRecord getStageSound(@Nullable WeaponStage stage, ItemStack stack) {
        return sounds.get(stage);
    }

    private static final Map<Integer, WeaponStage> reloadStages = Map.of(
            68, WeaponStage.MAGOUT,
            45, WeaponStage.MAGIN,
            20, WeaponStage.BOLTBACK,
            1, WeaponStage.BOLTFORWARD
    );

    @Override
    public @Nullable WeaponStage getReloadStageFromTick(int reloadTick) {
        return reloadStages.get(reloadTick);
    }
}
