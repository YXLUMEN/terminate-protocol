package lumen.terminate_protocol.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BallisticProjectileEntity extends ProjectileEntity {
    private static final TrackedData<Float> GRAVITY_MODIFIER = DataTracker.registerData(BallisticProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DRAG_COEFFICIENT = DataTracker.registerData(BallisticProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> LIFE = DataTracker.registerData(BallisticProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Vec3d windInfluence = Vec3d.ZERO;
    private boolean hasGravity = true;
    private int age;

    public BallisticProjectileEntity(EntityType<? extends BallisticProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(GRAVITY_MODIFIER, 0.05f);
        builder.add(DRAG_COEFFICIENT, 0.99f);
        builder.add(LIFE, 100);
    }

    @Override
    public void tick() {
        if (!this.hasNoGravity() && this.hasGravity) {
            Vec3d velocity = this.getVelocity();
            float gravity = this.dataTracker.get(GRAVITY_MODIFIER);
            this.setVelocity(velocity.add(0, -gravity, 0));
        }

        Vec3d velocity = this.getVelocity();
        float drag = this.dataTracker.get(DRAG_COEFFICIENT);
        this.setVelocity(velocity.multiply(drag));

        if (!this.windInfluence.equals(Vec3d.ZERO)) {
            this.setVelocity(this.getVelocity().add(windInfluence));
        }

        super.tick();

        if (++this.age > this.dataTracker.get(LIFE)) {
            this.discard();
        }
    }

    public BallisticProjectileEntity configure(float gravity, float drag, int lifeTicks) {
        this.dataTracker.set(GRAVITY_MODIFIER, gravity);
        this.dataTracker.set(DRAG_COEFFICIENT, drag);
        this.dataTracker.set(LIFE, lifeTicks);
        return this;
    }

    public BallisticProjectileEntity setWindInfluence(Vec3d wind) {
        this.windInfluence = wind;
        return this;
    }

    public BallisticProjectileEntity setGravityEnabled(boolean enabled) {
        this.hasGravity = enabled;
        return this;
    }
}