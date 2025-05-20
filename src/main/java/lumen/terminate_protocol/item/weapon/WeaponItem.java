package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import lumen.terminate_protocol.util.weapon.WeaponCooldownAccessor;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
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
import org.jetbrains.annotations.Nullable;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.findItemSlot;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.getPlayerLookVec;

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

        WeaponCooldownManager manager = ((WeaponCooldownAccessor) player).getWeaponCooldownManager();
        if (manager.isCoolingDown(this)) {
            manager.set(this, 0);
            stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
        }

        stack.setDamage(currentAmmo + 1);

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = player.getEyePos();

        this.raycaster.rayCast((ServerWorld) world, player, muzzlePos, lookVec);

        boolean lowAmmo = ((float) currentAmmo / maxAmmo) > 0.65f;
        ISoundRecord record = this.getStageSound(lowAmmo ? WeaponStage.FIRE_LOW_AMMO : WeaponStage.FIRE);
        if (record == null) return;

        playSoundRecord(record, world, player, player.getPos());
    }

    public void doReload(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient || stack.getDamage() == 0) return;
        WeaponCooldownManager manager = ((WeaponCooldownAccessor) player).getWeaponCooldownManager();

        if (manager.isCoolingDown(this)) return;

        manager.set(this, this.weaponSettings.getReloadTick());
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, true);
    }

    private void restoreAmmo(ItemStack stack, PlayerEntity player) {
        stack.set(TPComponentTypes.WPN_RELOADING_TYPE, false);
        ((WeaponCooldownAccessor) player).getWeaponCooldownManager().set(this, 0);

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
            this.reloadAction(world, (PlayerEntity) entity, stack, selected);
        }
    }

    private void reloadAction(World world, PlayerEntity player, ItemStack stack, boolean selected) {
        WeaponCooldownManager manager = ((WeaponCooldownAccessor) player).getWeaponCooldownManager();

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
        ISoundRecord record = this.getStageSound(stage);
        if (record == null) return;

        playSoundRecord(record, world, null, player.getPos());
    }

    private static void playSoundRecord(ISoundRecord record, World world, @Nullable PlayerEntity player, Vec3d pos) {
        if (record instanceof SoundHelper.SingleSound(SoundEvent sound, float volume, float pitch)) {
            world.playSound(player, pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, volume, pitch);
            return;
        }

        if (record instanceof SoundHelper.MultiSound(java.util.List<SoundHelper.SingleSound> sounds)) {
            sounds.forEach(singleSound -> world.playSound(player,
                    pos.x, pos.y, pos.z,
                    singleSound.sound(), SoundCategory.PLAYERS, singleSound.volume(), singleSound.pitch()));
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
