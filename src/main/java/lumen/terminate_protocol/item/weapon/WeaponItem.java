package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.RayCasterTools.findItemSlot;
import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public abstract class WeaponItem extends Item implements IWeaponSettings {
    private final WeaponSettings weaponSettings;
    private final TrajectoryRayCaster raycaster;
    protected final Random random = Random.create();

    public WeaponItem(Settings settings, WeaponSettings weaponSettings, TrajectoryRayCaster raycaster) {
        super(settings.maxCount(1));
        this.weaponSettings = weaponSettings;
        this.raycaster = raycaster;
    }

    public WeaponSettings getSettings() {
        return this.weaponSettings;
    }

    public void doFire(World world, PlayerEntity player, ItemStack stack) {
        int currentAmmo = stack.getDamage();
        int maxAmmo = stack.getMaxDamage();

        if (world.isClient || currentAmmo >= maxAmmo) return;

        if (player.getItemCooldownManager().isCoolingDown(this) && !stack.getOrDefault(TPComponentTypes.WPN_PULLBOLT_TYPE, false)) {
            player.getItemCooldownManager().set(this, 0);
            stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
        }

        stack.setDamage(currentAmmo + 1);

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = player.getEyePos();

        this.raycaster.rayCast((ServerWorld) world, player, muzzlePos, lookVec);
        if (((WeaponItem) stack.getItem()).getSettings().getRecoilType() == 1) {
            player.getItemCooldownManager().set(this, 20);
            stack.set(TPComponentTypes.WPN_PULLBOLT_TYPE, true);
        }

        boolean lowAmmo = ((float) currentAmmo / maxAmmo) > 0.65f;
        SoundEvent sound = this.getStageSound(lowAmmo ? WeaponStage.FIRE_LOW_AMMO : WeaponStage.FIRE);
        if (sound == null) return;

        world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
    }

    public void doReload(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient || stack.getDamage() == 0 || player.getItemCooldownManager().isCoolingDown(this)) return;

        player.getItemCooldownManager().set(this, this.weaponSettings.getReloadTick());
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, true);

        SoundEvent sound = this.getStageSound(WeaponStage.MAGOUT);
        if (sound == null) return;
        world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
    }

    private void restoreAmmo(ItemStack stack, PlayerEntity player) {
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
        player.getItemCooldownManager().set(this, 0);

        if (player.isCreative()) {
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
            this.reloadAction(world, (PlayerEntity) entity, stack);
        }

        if (stack.getOrDefault(TPComponentTypes.WPN_PULLBOLT_TYPE, false)) {
            PlayerEntity player = (PlayerEntity) entity;
            float reloadTick = player.getItemCooldownManager().getCooldownProgress(this, 0.0f);
            if (reloadTick > 0.5f) return;

            WeaponStage stage = this.getReloadStage(reloadTick);
            if (stage == WeaponStage.BOLTFORWARD) stack.set(TPComponentTypes.WPN_PULLBOLT_TYPE, false);

            SoundEvent sound = this.getStageSound(stage);
            if (sound == null) return;
            world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
        }
    }

    private void reloadAction(World world, PlayerEntity player, ItemStack stack) {
        if (!player.getMainHandStack().equals(stack)) {
            player.getItemCooldownManager().set(this, 0);
            stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
            return;
        }

        float reloadTick = player.getItemCooldownManager().getCooldownProgress(this, 0.0f);

        WeaponStage stage = this.getReloadStage(reloadTick);
        boolean quickCharge = stack.getDamage() < stack.getMaxDamage();

        if (stage == (quickCharge ? WeaponStage.MAGIN : WeaponStage.BOLTFORWARD)) {
            restoreAmmo(stack, player);
        }

        if (quickCharge && stage != WeaponStage.MAGIN) return;
        SoundEvent sound = this.getStageSound(stage);
        if (sound == null) return;
        world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
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
