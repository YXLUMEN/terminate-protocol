package lumen.terminate_protocol.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;

public class SmokeGrenadeEntity extends AbstractGrenadeEntity {
    private static final int DEFAULT_FUSE = 60;
    private static final short MAX_BOUNCES = 4;

    public SmokeGrenadeEntity(EntityType<? extends SmokeGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public SmokeGrenadeEntity(World world, LivingEntity owner) {
        super(TPEntities.SMOKE_GRENADE_ENTITY, world, owner);
    }

    public SmokeGrenadeEntity(World world, double x, double y, double z) {
        super(TPEntities.SMOKE_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return TPItems.SMOKE_GRENADE;
    }

    @Override
    protected void explode() {
        World world = this.getWorld();
        if (world.isClient) return;
        ServerWorld serverWorld = (ServerWorld) world;

        BlockPos pos = this.getBlockPos();

        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                pos.getX(), pos.getY(), pos.getZ(),
                1,
                0, 0, 0, 0);

        serverWorld.playSound(null, getBlockPos(),
                TPSoundEvents.GAS, SoundCategory.BLOCKS, 0.8f, 1.0f);

        SmokeEffectAreaEntity effectArea = new SmokeEffectAreaEntity(world, pos, 8);
        serverWorld.spawnEntity(effectArea);
    }

    @Override
    protected int getDefaultFuse() {
        return DEFAULT_FUSE;
    }

    @Override
    protected short getMaxBounces() {
        return MAX_BOUNCES;
    }
}
