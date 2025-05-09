package lumen.terminate_protocol.util;

import lumen.terminate_protocol.TerminateProtocol;
import lumen.terminate_protocol.damage_type.TPDamageTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;


public class TrajectoryRayCaster {
    public static final float DEFAULT_HARD_THRESHOLD = 1.0f;
    public static final short DEFAULT_MAX_HIT = 3;
    public static final short DEFAULT_BASE_RAY_LENGTH = 32;
    public static final float DEFAULT_BOUNCE_DAMAGE_LOSS = 0.15f;
    public static final float DEFAULT_BOUNCE_CHANCE = 1;
    public static final float DEFAULT_SCATTER_ANGLE = 0.1f;
    public static final float DEFAULT_DAMAGE_INFLUENCE = 0.3f;
    public static final float DEFAULT_BOUNCE_RANDOM_VARIATION = 0.2f;
    public static final float DEFAULT_PENETRATE_CHANCE = 1.0f;
    public static final float DEFAULT_PENETRATE_HARD_THRESHOLD = 1.2f;
    public static final float DEFAULT_ENTITY_DETECT_RADIUS = 0.4f;
    public static final float DEFAULT_BOUNCE_POSITION_OFFSET = 0.1f;
    public static final float DEFAULT_PENETRATION_POSITION_OFFSET = 0.3f;

    private RegistryKey<DamageType> damageType = TPDamageTypes.FRAGMENT_HIT;

    private short maxHit = DEFAULT_MAX_HIT;
    private float hardThreshold = DEFAULT_HARD_THRESHOLD;
    private float bounceChance = DEFAULT_BOUNCE_CHANCE;
    private int baseRayLength = DEFAULT_BASE_RAY_LENGTH;
    private float bounceDamageLoss = DEFAULT_BOUNCE_DAMAGE_LOSS;
    private float scatterAngle = DEFAULT_SCATTER_ANGLE;
    private float damageInfluence = DEFAULT_DAMAGE_INFLUENCE;
    private float bounceRandomVariation = DEFAULT_BOUNCE_RANDOM_VARIATION;
    private float penetrateChance = DEFAULT_PENETRATE_CHANCE;
    private float penetrateHardThreshold = DEFAULT_PENETRATE_HARD_THRESHOLD;
    private float entityDetectRadius = DEFAULT_ENTITY_DETECT_RADIUS;
    private float bouncePositionOffset = DEFAULT_BOUNCE_POSITION_OFFSET;
    private float penetrationPositionOffset = DEFAULT_PENETRATION_POSITION_OFFSET;

    public TrajectoryRayCaster() {
    }

    public TrajectoryRayCaster baseRayLength(int baseRayLength) {
        this.baseRayLength = baseRayLength;
        return this;
    }

    public TrajectoryRayCaster damageType(RegistryKey<DamageType> damageType) {
        this.damageType = damageType;
        return this;
    }

    public TrajectoryRayCaster bounceHardThreshold(float hardThreshold) {
        this.hardThreshold = hardThreshold;
        return this;
    }

    public TrajectoryRayCaster maxHit(short maxHit) {
        this.maxHit = maxHit;
        return this;
    }

    public TrajectoryRayCaster bounceChance(float bounceChance) {
        this.bounceChance = bounceChance;
        return this;
    }

    public TrajectoryRayCaster bounceDamageLoss(float bounceDamageLoss) {
        this.bounceDamageLoss = bounceDamageLoss;
        return this;
    }

    public TrajectoryRayCaster bounceScatterAngle(float scatterAngle) {
        this.scatterAngle = scatterAngle;
        return this;
    }

    public TrajectoryRayCaster damageInfluence(float f) {
        this.damageInfluence = f;
        return this;
    }

    public TrajectoryRayCaster bounceRandomVariation(float f) {
        this.bounceRandomVariation = f;
        return this;
    }

    public TrajectoryRayCaster penetrateChance(float f) {
        this.penetrateChance = f;
        return this;
    }

    public TrajectoryRayCaster penetrateHardThreshold(float f) {
        this.penetrateHardThreshold = f;
        return this;
    }

    public TrajectoryRayCaster withEntityDetectRadius(float radius) {
        this.entityDetectRadius = radius;
        return this;
    }

    public TrajectoryRayCaster withBouncePositionOffset(float offset) {
        this.bouncePositionOffset = offset;
        return this;
    }

    public TrajectoryRayCaster withPenetrationPositionOffset(float offset) {
        this.penetrationPositionOffset = offset;
        return this;
    }

    public void rayCast(ServerWorld world, Entity attacker, Vec3d start, Vec3d dir, float damage) {
        Random random = world.random;

        Vec3d currentPos = new Vec3d(start.x, start.y, start.z);
        Vec3d currentDir = dir.normalize();

        float remainingDamage = damage;
        int remainingHit = maxHit;

        while (remainingHit > 0 && remainingDamage > 0.5f) {
            Vec3d endPos = currentPos.add(currentDir.multiply(baseRayLength));

            EntityHitResult entityHit = ProjectileUtil.raycast(
                    attacker, currentPos, endPos,
                    new Box(currentPos, endPos).expand(entityDetectRadius),
                    e -> !e.isSpectator() && e.isAlive() && e.canHit(),
                    baseRayLength * baseRayLength
            );

            if (entityHit != null) {
                handleDamage(world, attacker, entityHit.getEntity(), remainingDamage);
                return;
            }

            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    currentPos, endPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    attacker
            ));

            if (blockHit.getType() != HitResult.Type.BLOCK) {
                break;
            }
            remainingHit--;

            BlockState blockState = world.getBlockState(blockHit.getBlockPos());
            final float originHardness = blockState.getHardness(world, blockHit.getBlockPos());
            final float hardness = originHardness > 0 ? originHardness : 100.0f;

            Direction face = blockHit.getSide();
            final Vec3d normal = new Vec3d(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ()).normalize();
            if (shouldBounce(currentDir, normal, hardness, remainingDamage, random)) {
                // 反弹
                currentDir = calculateReflection(currentDir, normal, random).normalize();
                currentPos = blockHit.getPos().add(currentDir.multiply(bouncePositionOffset));
                remainingDamage *= (1 - bounceDamageLoss);

                world.spawnParticles(ParticleTypes.SMOKE,
                        currentPos.x, currentPos.y, currentPos.z, 1, 0, 0, 0, 0);

            } else if (tryPenetrate(hardness, remainingDamage, random)) {
                // 穿透
                currentPos = blockHit.getPos().add(currentDir.multiply(penetrationPositionOffset));
                remainingDamage *= Math.max(0.3f, 1 - (hardness * 0.1f));

                world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                        currentPos.x, currentPos.y, currentPos.z, 4, 0, 0, 0, 0.01);

            } else break;
        }
    }

    private boolean shouldBounce(Vec3d incoming, Vec3d normal, float hardness, float damage, Random random) {
        float cosTheta = (float) Math.abs(incoming.normalize().dotProduct(normal));
        float angleFactor = (float) Math.sqrt(1 - cosTheta * cosTheta);

        float hardnessFactor = Math.min(1, hardness / hardThreshold);

        float damageFactor = 1 / (1 + damageInfluence * damage);

        float randomVariation = 1 + (random.nextFloat() * 2 - 1) * bounceRandomVariation;

        float bounceProbability = MathHelper.clamp(
                (0.5f * hardnessFactor + 0.4f * angleFactor + 0.1f * damageFactor) * randomVariation * bounceChance,
                0.1f, 0.8f);

        return random.nextFloat() < bounceProbability;
    }

    private boolean tryPenetrate(float hardness, float damage, Random random) {
        TerminateProtocol.LOGGER.info("{}", hardness);
        if (penetrateHardThreshold < hardness) return false;
        float randomVariation = (0.7f + random.nextFloat() * 0.3f);
        return damage * randomVariation * penetrateChance > hardness;
    }

    private Vec3d calculateReflection(Vec3d incoming, Vec3d normal, Random random) {
        Vec3d reflected = incoming.subtract(normal.multiply(2 * incoming.dotProduct(normal)));

        float angle = random.nextFloat() * scatterAngle;
        return reflected.rotateX(angle * (random.nextBoolean() ? 1 : -1))
                .rotateY(angle * (random.nextBoolean() ? 1 : -1));
    }

    private void handleDamage(ServerWorld world, Entity attacker, Entity target, float damage) {
        if (target instanceof LivingEntity living) {
            living.hurtTime = 0;
            living.timeUntilRegen = 2;
        }

        target.damage(world.getDamageSources().create(this.damageType, attacker), damage);
    }
}
