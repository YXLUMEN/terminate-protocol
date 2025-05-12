package lumen.terminate_protocol.entity;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.RayCasterTools.getRandomDirection;

public class FragGrenadeEntity extends AbstractGrenadeEntity {
    private static final float POWER = 3.0f;
    private static final short FRAG_COUNT = 32;
    private static final TrajectoryRayCaster RAY_CASTER = new TrajectoryRayCaster().maxHit((short) 3).baseDamage(6.0f);

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

        if (!world.isClient) {
            world.createExplosion(this,
                    this.getX(), this.getY(), this.getZ(),
                    POWER,
                    false,
                    World.ExplosionSourceType.NONE
            );

            Vec3d startPos = this.getPos().add(0, 0.5, 0);
            for (int i = 0; i < FRAG_COUNT; i++) {
                Vec3d randomDir = getRandomDirection(this.random);
                RAY_CASTER.rayCast((ServerWorld) world, this, startPos, randomDir);
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
