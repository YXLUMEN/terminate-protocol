package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.network.GunFireSyncS2CPayload;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public abstract class AbstractGunItem extends Item implements IGunSettings {
    public AbstractGunItem(Settings settings) {
        super(settings.maxCount(1).component(TPComponentTypes.GUN_RELOADING_TYPE, false));
    }

    public void onFire(World world, PlayerEntity player) {
        if (world.isClient || player.getItemCooldownManager().isCoolingDown(this)) return;
        ItemStack stack = player.getMainHandStack();

        if (stack.getDamage() >= stack.getMaxDamage()) return;
        stack.setDamage(stack.getDamage() + 1);

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = player.getEyePos();

        this.getTrajectory().rayCast((ServerWorld) world, player, muzzlePos, lookVec);
        this.handlerSync(player, muzzlePos, lookVec);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), this.getShootSound(), SoundCategory.PLAYERS);
    }

    public void onReload(World world, PlayerEntity player) {
        if (world.isClient) return;

        ItemStack stack = player.getMainHandStack();
        if (stack.getDamage() == 0) return;

        player.getItemCooldownManager().set(this, this.getReloadTick());
        stack.set(TPComponentTypes.GUN_RELOADING_TYPE, true);
    }

    private void restoreAmmo(ItemStack stack, PlayerEntity player) {
        if (player.isCreative()) {
            stack.setDamage(0);
            return;
        }

        int ammoSlot = findAmmoSlot(player);
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
        if (stack.getOrDefault(TPComponentTypes.GUN_RELOADING_TYPE, false)) {
            if (!(entity instanceof PlayerEntity player)) return;
            if (player.getItemCooldownManager().isCoolingDown(this)) return;
            stack.set(TPComponentTypes.GUN_RELOADING_TYPE, false);
            restoreAmmo(stack, player);
        }
    }

    private void handlerSync(PlayerEntity attacker, Vec3d start, Vec3d end) {
        var payload = new GunFireSyncS2CPayload(start, end);
        for (ServerPlayerEntity player : PlayerLookup.tracking(attacker)) {
            if (player == null || player.equals(attacker)) continue;
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static int findAmmoSlot(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.GUNPOWDER)) return i;
        }
        return -1;
    }

    public static Vec3d getMuzzleOffset(PlayerEntity player, Vec3d lookVec) {
        Vec3d rightOffset = new Vec3d(-lookVec.z, 0, lookVec.x).normalize().multiply(0.3);
        return player.getEyePos().add(rightOffset).add(0, -0.3, 0);
    }

    protected SoundEvent getShootSound() {
        return SoundEvents.ENTITY_ARROW_SHOOT;
    }

    protected abstract TrajectoryRayCaster getTrajectory();

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
}
