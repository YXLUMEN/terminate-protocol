package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.api.ICast;
import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.*;

public abstract class WeaponItem extends Item implements IWeaponSettings {
    private final WeaponSettings weaponSettings;
    private final ICast caster;
    protected final Random random = Random.create();

    public WeaponItem(Settings settings, WeaponSettings weaponSettings, TrajectoryRayCaster caster) {
        super(settings.maxCount(1));
        this.weaponSettings = weaponSettings;
        this.caster = caster;
    }

    public WeaponSettings getSettings() {
        return this.weaponSettings;
    }

    public void onFire(World world, ServerPlayerEntity player, ItemStack stack) {
        int currentAmmo = stack.getDamage();

        if (world.isClient || currentAmmo >= stack.getMaxDamage()) return;

        WeaponCooldownManager manager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (manager.isCoolingDown(this)) {
            // interrupt reloading
            if (stack.getOrDefault(TPComponentTypes.WPN_RELOADING_TYPE, false)) {
                manager.set(this, 0);
                stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
            } else {
                return;
            }
        }

        stack.setDamage(currentAmmo + 1);

        boolean isAiming = ((WeaponAccessor) player).terminate_protocol$getWpnAiming();
        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = isAiming ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        this.caster.start(world, player, muzzlePos, lookVec);
        manager.set(this, Math.max(0, this.weaponSettings.getFireRate()));

        ISoundRecord record = this.getStageSound(WeaponStage.FIRE, stack);
        if (record == null) return;

        playSoundRecord(record, world, player, player.getPos());
    }

    public void onReload(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient || stack.getDamage() == 0) return;
        WeaponCooldownManager manager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (manager.isCoolingDown(this)) return;

        manager.set(this, this.weaponSettings.getReloadTick());
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, true);
    }

    private void restoreAmmo(ItemStack stack, PlayerEntity player) {
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
        ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager().set(this, 0);

        if (player.isCreative() || findItemSlot(player, TPItems.DEBUG_AMMO) != -1) {
            stack.setDamage(0);
            return;
        }

        int ammoSlot = findItemSlot(player, this.weaponSettings.getAmmoType());
        if (ammoSlot == -1) return;

        int damage = stack.getDamage();
        int maxRepair = player.getInventory().getStack(ammoSlot).getCount();
        int repairAmount = Math.min(damage, maxRepair);

        player.getInventory().getStack(ammoSlot).decrement(repairAmount);
        stack.setDamage(stack.getDamage() - repairAmount);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !entity.isPlayer()) return;

        if (stack.getOrDefault(TPComponentTypes.WPN_RELOADING_TYPE, false)) {
            this.reloadAction(world, (PlayerEntity) entity, stack, selected);
        }
    }

    private void reloadAction(World world, PlayerEntity player, ItemStack stack, boolean selected) {
        WeaponCooldownManager manager = ((WeaponAccessor) player).terminate_protocol$getWpnCooldownManager();

        if (!selected) {
            manager.set(this, 0);
            stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
            return;
        }

        int reloadTick = manager.getCooldownTicks(this);
        WeaponStage stage = this.getReloadStageFromTick(reloadTick);
        if (stage == null) return;

        boolean quickCharge = stack.getDamage() < stack.getMaxDamage();

        if (stage == (quickCharge ? WeaponStage.MAGIN : WeaponStage.BOLTFORWARD)) {
            restoreAmmo(stack, player);
        }

        if (quickCharge && (stage == WeaponStage.BOLTBACK || stage == WeaponStage.BOLTFORWARD)) return;
        ISoundRecord record = this.getStageSound(stage, stack);
        if (record == null) return;

        playSoundRecord(record, world, null, player.getPos());
    }

    private static void playSoundRecord(ISoundRecord record, World world, @Nullable PlayerEntity player, Vec3d pos) {
        switch (record) {
            case SoundHelper.SingleSound(SoundEvent sound, float volume, float pitch) ->
                    world.playSound(player, pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, volume, pitch);

            case SoundHelper.MultiSound(java.util.List<SoundHelper.SingleSound> sounds) ->
                    sounds.forEach(singleSound -> world.playSound(player,
                            pos.x, pos.y, pos.z,
                            singleSound.sound(), SoundCategory.PLAYERS, singleSound.volume(), singleSound.pitch()));
            case null, default -> {
            }
        }

    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }
}
