package lumen.terminate_protocol.entity.grenade;

import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getRandomDirection;

public class FragGrenadeEntity extends AbstractGrenadeEntity {
    private static final float POWER = 3.0f;
    private static final short FRAG_COUNT = 32;
    private static final TrajectoryRayCaster RAY_CASTER = new TrajectoryRayCaster().baseDamage(6.0f);

    public FragGrenadeEntity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public FragGrenadeEntity(World world, LivingEntity owner) {
        super(TPEntities.FRAG_GRENADE_ENTITY, world, owner);
    }

    public FragGrenadeEntity(World world, double x, double y, double z) {
        super(TPEntities.FRAG_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected void explode() {
        World world = this.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.createExplosion(this,
                    this.getX(), this.getY(), this.getZ(),
                    POWER,
                    false,
                    World.ExplosionSourceType.NONE
            );

            serverWorld.playSound(null, getBlockPos(),
                    SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS, 1.0f, 1.0f);
            serverWorld.spawnParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(),
                    1, 0, 0, 0, 0);

            Vec3d startPos = this.getPos().add(0, 0.5, 0);
            for (int i = 0; i < FRAG_COUNT; i++) {
                Vec3d randomDir = getRandomDirection(this.random);
                RAY_CASTER.start(world, this, startPos, randomDir);
            }
        }

        this.discard();
    }

    @Override
    protected int getDefaultFuse() {
        return 60;
    }

    @Override
    protected short getMaxBounces() {
        return 3;
    }

    @Override
    protected Item getDefaultItem() {
        return TPItems.FRAG_GRENADE;
    }
}
