package lumen.terminate_protocol.item;

import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public abstract class AbstractGunItem extends Item {
    public AbstractGunItem(Settings settings) {
        super(settings);
    }

    public void onFire(World world, PlayerEntity player) {
        if (world.isClient || player.getItemCooldownManager().isCoolingDown(this)) return;
        ItemStack stack = player.getMainHandStack();

        if (stack.getDamage() >= stack.getMaxDamage()) return;
        stack.setDamage(stack.getDamage() + 1);
        this.handleShoot((ServerWorld) world, player);
    }

    public void onReload(World world, PlayerEntity player) {
        if (world.isClient || player.getItemCooldownManager().isCoolingDown(this)) return;
        player.getItemCooldownManager().set(this, this.getReloadTick());

        ItemStack stack = player.getMainHandStack();

        if (stack.getDamage() == 0) return;
        this.loadAmmo(player, stack);
    }

    private void handleShoot(ServerWorld world, PlayerEntity player) {
        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = getMuzzleOffset(player, lookVec);

        this.getRayCaster().rayCast(world, player, muzzlePos, lookVec, getFireDamage());
        world.playSound(null, player.getX(), player.getY(), player.getZ(), this.getShootSound(), SoundCategory.PLAYERS);
    }

    private void loadAmmo(PlayerEntity player, ItemStack stack) {
        if (player.isInCreativeMode()) {
            stack.setDamage(0);
            return;
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack invStack = player.getInventory().getStack(i);
            if (invStack.isOf(getReloadItem())) {
                invStack.decrement(getReloadConsume());

                stack.setDamage(0);
                return;
            }
        }
    }

    public static Vec3d getMuzzleOffset(PlayerEntity player, Vec3d lookVec) {
        Vec3d rightOffset = new Vec3d(-lookVec.z, 0, lookVec.x).normalize().multiply(0.3);
        return player.getEyePos().add(rightOffset).add(0, -0.3, 0);
    }

    public Item getReloadItem() {
        return Items.GUNPOWDER;
    }

    protected SoundEvent getShootSound() {
        return SoundEvents.ENTITY_ARROW_SHOOT;
    }

    public abstract TrajectoryRayCaster getRayCaster();

    public abstract float getFireDamage();

    public abstract int getReloadConsume();

    public abstract int getFireRant();

    public abstract float getVerticalRecoil(Random random);

    public abstract float getHorizontalRecoil(Random random);

    public abstract int getReloadTick();

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
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0F - (float) stack.getDamage() * 13.0F / (float) stack.getMaxDamage());
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) (stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage());
        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }
}
