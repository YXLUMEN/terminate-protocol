package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.entity.HomingMissileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getPlayerLookVec;

public class RocketLauncherItem extends Item {
    private static final int MAX_USE_TIME = 30;

    public RocketLauncherItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            LivingEntity target = getAimTarget(user);

            HomingMissileEntity missile = new HomingMissileEntity(world, user, target);
            missile.setYaw(user.getYaw());
            missile.setPitch(user.getPitch());
            missile.setVelocity(user, user.getPitch(), user.getYaw(), 0, 3.0f, 1.0f);
            world.spawnEntity(missile);
        }

        return stack;
    }

    @Nullable
    private LivingEntity getAimTarget(LivingEntity user) {
        if (!user.isPlayer()) return null;
        PlayerEntity player = (PlayerEntity) user;

        Vec3d playerPos = player.getRotationVec(1.0f).multiply(0.5);
        Vec3d endPos = playerPos.add(getPlayerLookVec(player).multiply(60));

        EntityHitResult entityHit = ProjectileUtil.raycast(
                player, playerPos, endPos,
                new Box(playerPos, endPos).expand(1),
                e -> !e.isSpectator() && e.isAlive() && e.canHit(),
                playerPos.squaredDistanceTo(endPos)
        );

        if (entityHit == null) return null;

        if (entityHit.getEntity() instanceof LivingEntity living) return living;
        return null;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
