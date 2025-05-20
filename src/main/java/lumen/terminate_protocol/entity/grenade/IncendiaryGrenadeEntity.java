package lumen.terminate_protocol.entity.grenade;

import lumen.terminate_protocol.entity.TPEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import lumen.terminate_protocol.item.TPItems;

public class IncendiaryGrenadeEntity extends AbstractGrenadeEntity {
    private static final short EFFECT_DURATION = 6;

    public IncendiaryGrenadeEntity(EntityType<? extends IncendiaryGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public IncendiaryGrenadeEntity(World world, LivingEntity owner) {
        super(TPEntities.INCENDIARY_GRENADE_ENTITY, world, owner);
    }

    public IncendiaryGrenadeEntity(World world, double x, double y, double z) {
        super(TPEntities.INCENDIARY_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void explode() {
        World world = this.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            world.createExplosion(
                    this,
                    Explosion.createDamageSource(this.getWorld(), this),
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    3.0F,
                    true,
                    World.ExplosionSourceType.NONE,
                    ParticleTypes.LAVA,
                    ParticleTypes.EXPLOSION,
                    SoundEvents.ENTITY_GENERIC_EXPLODE
            );

            Box explosionBox = new Box(this.getBlockPos()).expand(EFFECT_DURATION);
            serverWorld.getEntitiesByClass(Entity.class, explosionBox, entity -> entity instanceof LivingEntity)
                    .forEach(entity -> entity.setOnFireFor(20));
        }

        this.discard();
    }

    @Override
    protected int getDefaultFuse() {
        return 80;
    }

    @Override
    protected short getMaxBounces() {
        return 2;
    }

    @Override
    protected Item getDefaultItem() {
        return TPItems.INCENDIARY_GRENADE;
    }
}
