package lumen.terminate_protocol.entity.grenade;


import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getRandomDirection;


public class Howitzer152Entity extends AbstractGrenadeEntity {
    private static final float POWER = 64;
    private static final short FRAG_COUNT = 256;
    private static final TrajectoryRayCaster RAY_CASTER = new TrajectoryRayCaster()
            .maxHit((short) 2).baseDamage(160.0f).penetrateChance(3.0f);

    public Howitzer152Entity(EntityType<? extends AbstractGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public Howitzer152Entity(World world, LivingEntity owner) {
        super(TPEntities.HOWITZER_152_ENTITY, world, owner);
    }

    public Howitzer152Entity(World world, double x, double y, double z) {
        super(TPEntities.HOWITZER_152_ENTITY, world, x, y, z);
    }

    @Override
    protected void explode() {
        World world = this.getWorld();

        if (!world.isClient) {
            world.createExplosion(this,
                    this.getX(), this.getY(), this.getZ(),
                    POWER,
                    true,
                    World.ExplosionSourceType.NONE
            );

            Vec3d startPos = this.getPos().add(0, 0.2, 0);
            for (int i = 0; i < FRAG_COUNT; i++) {
                Vec3d randomDir = getRandomDirection(this.random);
                RAY_CASTER.start((ServerWorld) world, this, startPos, randomDir);
            }
        }

        this.discard();
    }

    @Override
    protected int getDefaultFuse() {
        return 20;
    }

    @Override
    protected short getMaxBounces() {
        return 1;
    }

    @Override
    protected Item getDefaultItem() {
        return TPItems.HOWITZER_152;
    }

    @Override
    protected boolean isInstant() {
        return true;
    }
}
