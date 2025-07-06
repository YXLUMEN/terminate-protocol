package lumen.terminate_protocol.entity;

import lumen.terminate_protocol.util.track_algorithm.ConeTargetFinder;
import lumen.terminate_protocol.util.track_algorithm.CubicBezierTracker;
import lumen.terminate_protocol.util.track_algorithm.TrackAlgorithm;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getRandomDirection;

public class HomingMissileEntity extends ThrownEntity {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(HomingMissileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final ConeTargetFinder TARGET_FINDER = new ConeTargetFinder(15, 60.0f);
    private static final TrajectoryRayCaster RAY_CASTER = new TrajectoryRayCaster().baseDamage(8.0f);
    private static final float BASE_VELOCITY = 3.0f;
    private static final float POWER = 4.0f;
    private static final short FRAG_COUNT = 32;

    private final TrackAlgorithm TRACKER = new CubicBezierTracker(0.3f);
    private final Box BOUNDING_BOX = this.getBoundingBox().expand(1);

    @Nullable
    private LivingEntity target;
    private int searchCooldown;

    public HomingMissileEntity(EntityType<? extends HomingMissileEntity> entityType, World world) {
        super(entityType, world);
    }

    public HomingMissileEntity(World world, LivingEntity owner, @Nullable LivingEntity target) {
        super(TPEntities.HOMING_MISSILE_ENTITY, owner, world);
        this.target = target;
    }

    public HomingMissileEntity(World world, double x, double y, double z) {
        super(TPEntities.HOMING_MISSILE_ENTITY, x, y, z, world);
    }

    @Override
    public void tick() {
        super.tick();

        int fuse = this.dataTracker.get(FUSE) - 1;
        this.dataTracker.set(FUSE, fuse);

        if (this.getWorld().isClient) return;
        ServerWorld world = (ServerWorld) this.getWorld();

        if (fuse <= 0) {
            explode(world);
            return;
        }

        if (target == null) {
            if (searchCooldown-- <= 0) searchTarget();
            return;
        }

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        TRACKER.updatePosition(this.getPos(), targetPos, this.getVelocity());

        this.setVelocity(TRACKER.getAdjustedVelocity().multiply(BASE_VELOCITY));

        if (world.getOtherEntities(this, BOUNDING_BOX).stream()
                .anyMatch(e -> e instanceof LivingEntity && e != this.getOwner())) explode(world);
    }

    public void searchTarget() {
        searchCooldown = 4;

        if (this.getOwner() instanceof LivingEntity lOwner && this.getWorld() instanceof ServerWorld world) {
            this.target = TARGET_FINDER.findTarget(
                    world,
                    this.getPos(),
                    this.getVelocity().normalize(),
                    lOwner
            );
        }
    }

    private void explode(ServerWorld world) {
        world.createExplosion(this,
                this.getX(), this.getY(), this.getZ(),
                POWER,
                false,
                World.ExplosionSourceType.NONE
        );

        Vec3d startPos = this.getPos().add(0, 0.5, 0);
        for (int i = 0; i < FRAG_COUNT; i++) {
            Vec3d randomDir = getRandomDirection(this.random);
            RAY_CASTER.start(world, this, startPos, randomDir);
        }

        this.discard();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FUSE, 100);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.explode((ServerWorld) this.getWorld());
        }
    }

    @Override
    protected double getGravity() {
        return 0.01;
    }
}
