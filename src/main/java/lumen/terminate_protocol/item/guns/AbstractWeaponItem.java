package lumen.terminate_protocol.item.guns;

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
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public abstract class AbstractWeaponItem extends Item implements IWeaponSettings {
    private static final Vec3d DEFAULT_AIM_POS = new Vec3d(-0.52f, 0.14f, 0);

    public AbstractWeaponItem(Settings settings) {
        super(settings.maxCount(1).component(TPComponentTypes.GUN_RELOADING_TYPE, false));
    }

    public void onFire(World world, PlayerEntity player, ItemStack stack) {
        int currentAmmo = stack.getDamage();
        int maxAmmo = stack.getMaxDamage();

        if (world.isClient || currentAmmo >= maxAmmo) return;
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            player.getItemCooldownManager().set(this, 0);
            stack.set(TPComponentTypes.GUN_RELOADING_TYPE, false);
        }

        stack.setDamage(currentAmmo + 1);

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = player.getEyePos();

        this.getTrajectory().rayCast((ServerWorld) world, player, muzzlePos, lookVec);
        if (stack.getItem() instanceof IWeaponSound wpn) {
            boolean lowAmmo = ((float) currentAmmo / maxAmmo) > 0.7f;

            SoundEvent sound = wpn.getSounds(lowAmmo ? WeaponSoundStage.FIRE_LOW_AMMO : WeaponSoundStage.FIRE, world.getRandom());
            if (sound == null) return;

            world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
        }
    }

    public void onReload(World world, PlayerEntity player, ItemStack stack) {
        if (world.isClient || player.getItemCooldownManager().isCoolingDown(this) || stack.getDamage() == 0) return;

        player.getItemCooldownManager().set(this, this.getReloadTick());
        stack.set(TPComponentTypes.GUN_RELOADING_TYPE, true);

        if (stack.getItem() instanceof IWeaponSound wpn) {
            SoundEvent sound = wpn.getSounds(WeaponSoundStage.MAGOUT, world.getRandom());
            if (sound != null) {
                world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !entity.isPlayer()) return;

        if (stack.getOrDefault(TPComponentTypes.GUN_RELOADING_TYPE, false)) {
            PlayerEntity player = (PlayerEntity) entity;

            if (!player.getMainHandStack().equals(stack)) {
                player.getItemCooldownManager().set(this, 0);
                stack.set(TPComponentTypes.GUN_RELOADING_TYPE, false);
                return;
            }

            float reloadTick = player.getItemCooldownManager().getCooldownProgress(this, 0.0f);
            if (stack.getItem() instanceof IWeaponSound wpn) {
                WeaponSoundStage stage = wpn.getReloadStage(reloadTick);
                SoundEvent sound = wpn.getSounds(stage, world.getRandom());
                if (sound != null) {
                    world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
                }
            }

            if (reloadTick > 0.0f) return;
            stack.set(TPComponentTypes.GUN_RELOADING_TYPE, false);

            restoreAmmo(stack, player);
        }
    }

    private void restoreAmmo(ItemStack stack, PlayerEntity player) {
        if (player.isCreative()) {
            stack.setDamage(0);
            return;
        }

        int ammoSlot = findItemSlot(player, this.getAmmo());
        if (ammoSlot == -1) return;

        int damage = stack.getDamage();
        int maxRepair = player.getInventory().getStack(ammoSlot).getCount();
        int repairAmount = Math.min(damage, maxRepair);

        player.getInventory().getStack(ammoSlot).decrement(repairAmount);
        stack.setDamage(stack.getDamage() - repairAmount);
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

    protected abstract TrajectoryRayCaster getTrajectory();

    public Vec3d getAimPos() {
        return DEFAULT_AIM_POS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
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
