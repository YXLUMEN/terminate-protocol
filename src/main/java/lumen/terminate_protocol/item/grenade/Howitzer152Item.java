package lumen.terminate_protocol.item.grenade;

import lumen.terminate_protocol.entity.Howitzer152Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class Howitzer152Item extends AbstractGrenadeItem {
    private static final int COOLDOWN_TICKS = 200;

    public Howitzer152Item(Item.Settings settings) {
        super(settings);
    }


    @Override
    protected void createGrenade(World world, PlayerEntity user, ItemStack itemStack) {
        Howitzer152Entity entity = new Howitzer152Entity(world, user);
        entity.setItem(itemStack);
        entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.2F, 2.0f, 1.0F);
        world.spawnEntity(entity);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        Howitzer152Entity entity = new Howitzer152Entity(world, pos.getX(), pos.getY(), pos.getZ());
        entity.setItem(stack);
        return entity;
    }
}
