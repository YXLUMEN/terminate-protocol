package lumen.terminate_protocol.item.grenade;

import lumen.terminate_protocol.entity.grenade.FragGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class FragGrenadeItem extends AbstractGrenadeItem {
    private static final int COOLDOWN_TICKS = 20;

    public FragGrenadeItem(Item.Settings settings) {
        super(settings);
    }


    @Override
    protected void createGrenade(World world, PlayerEntity user, ItemStack itemStack) {
        float speed = user.isSneaking() ? 0.5F : 1.5F;
        FragGrenadeEntity entity = new FragGrenadeEntity(world, user);
        entity.setItem(itemStack);
        entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, speed, 1.0F);
        world.spawnEntity(entity);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        FragGrenadeEntity entity = new FragGrenadeEntity(world, pos.getX(), pos.getY(), pos.getZ());
        entity.setItem(stack);
        return entity;
    }
}
