package lumen.terminate_protocol.util.weapon;

import lumen.terminate_protocol.api.HitBoxType;
import lumen.terminate_protocol.api.ICast;
import lumen.terminate_protocol.api.TPDamageTypes;
import lumen.terminate_protocol.effect.TPEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.World;

import static lumen.terminate_protocol.util.weapon.PreciseHitHelper.DAMAGE_MULTIPLIERS;
import static lumen.terminate_protocol.util.weapon.WeaponHelper.syncTrack;


public class TrajectoryRayCaster implements ICast {
    private final RegistryKey<DamageType> damageType;
    private final boolean showTrack;
    private final boolean important;

    private final float damage;
    private final int maxHit;
    private final float entityDetectRadius;
    private final float hardThreshold;
    private final float bounceChance;
    private final int baseRayLength;
    private final float bounceDamageLoss;
    private final float scatterAngle;
    private final float damageInfluence;
    private final float bounceRandomVariation;
    private final float penetrateChance;
    private final float bouncePositionOffset;
    private final float healthBaseDamage;
    private final boolean seriousInjury;

    public TrajectoryRayCaster(TrajectoryRayCaster.Builder builder) {
        this.damageType = builder.damageType;
        this.showTrack = builder.showTrack;
        this.important = builder.important;
        this.damage = builder.damage;
        this.maxHit = builder.maxHit;
        this.entityDetectRadius = builder.entityDetectRadius;
        this.hardThreshold = builder.hardThreshold;
        this.bounceChance = builder.bounceChance;
        this.baseRayLength = builder.baseRayLength;
        this.bounceDamageLoss = builder.bounceDamageLoss;
        this.scatterAngle = builder.scatterAngle;
        this.damageInfluence = builder.damageInfluence;
        this.bounceRandomVariation = builder.bounceRandomVariation;
        this.penetrateChance = builder.penetrateChance;
        this.bouncePositionOffset = builder.bouncePositionOffset;
        this.healthBaseDamage = builder.healthBaseDamage;
        this.seriousInjury = builder.seriousInjury;
    }

    public void start(World world, Entity attacker, Vec3d start, Vec3d dir) {
        Random random = world.random;

        Vec3d currentPos = new Vec3d(start.x, start.y, start.z);
        Vec3d currentDir = dir.normalize();

        float remainingDamage = this.damage;
        int remainingHit = this.maxHit;

        while (remainingHit-- > 0 && remainingDamage > 0.5f) {
            Vec3d endPos = currentPos.add(currentDir.multiply(this.baseRayLength));

            EntityHitResult entityHit = ProjectileUtil.raycast(
                    attacker, currentPos, endPos,
                    new Box(currentPos, endPos).expand(this.entityDetectRadius),
                    e -> !e.isSpectator() && e.isAlive() && e.canHit(),
                    currentPos.squaredDistanceTo(endPos)
            );

            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    currentPos, endPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    attacker
            ));

            if (blockHit.getType() != HitResult.Type.BLOCK) {
                if (entityHit == null) {
                    if (this.showTrack) syncTrack(attacker, currentPos, endPos, this.important);
                    return;
                }

                this.handleDamage(world, attacker, entityHit, remainingDamage);
                if (this.showTrack) syncTrack(attacker, currentPos, entityHit.getPos(), this.important);
                return;
            }

            if (entityHit != null && currentPos.squaredDistanceTo(blockHit.getPos()) > currentPos.squaredDistanceTo(entityHit.getPos())) {
                this.handleDamage(world, attacker, entityHit, remainingDamage);
                if (this.showTrack) syncTrack(attacker, currentPos, entityHit.getPos(), this.important);
                return;
            }

            BlockState blockState = world.getBlockState(blockHit.getBlockPos());
            final float originHardness = blockState.getHardness(world, blockHit.getBlockPos());
            final float hardness = originHardness > 0 ? originHardness : 100.0f;

            Direction face = blockHit.getSide();
            final Vec3d normal = new Vec3d(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ()).normalize();

            if (this.showTrack) syncTrack(attacker, currentPos, blockHit.getPos(), this.important);
            if (this.shouldBounce(currentDir, normal, hardness, remainingDamage, random)) {
                currentDir = this.calculateReflection(currentDir, normal, random).normalize();
                currentPos = blockHit.getPos().add(currentDir.multiply(this.bouncePositionOffset));
                remainingDamage *= (1 - this.bounceDamageLoss);

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                            currentPos.x, currentPos.y, currentPos.z, 1, 0, 0, 0, 0);
                }

            } else if (this.tryPenetrate(hardness, remainingDamage, random)) {
                // 穿透
                currentPos = blockHit.getPos().add(currentDir);
                remainingDamage *= Math.max(0.3f, 1 - (hardness * 0.2f));

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                            currentPos.x, currentPos.y, currentPos.z, 4, 0, 0, 0, 0.01);
                }

            } else break;
        }
    }

    private boolean shouldBounce(Vec3d incoming, Vec3d normal, float hardness, float damage, Random random) {
        float cosTheta = (float) Math.abs(incoming.normalize().dotProduct(normal));
        float angleFactor = (float) Math.sqrt(1 - cosTheta * cosTheta);

        float hardnessFactor = Math.min(1, hardness / this.hardThreshold);

        float damageFactor = 1 / (1 + this.damageInfluence * damage);

        float randomVariation = 1 + (random.nextFloat() * 2 - 1) * this.bounceRandomVariation;

        float bounceProbability = MathHelper.clamp(
                (0.5f * hardnessFactor + 0.4f * angleFactor + 0.1f * damageFactor) * randomVariation * this.bounceChance,
                0.0f, 1.0f);

        return random.nextFloat() < bounceProbability;
    }

    private boolean tryPenetrate(float hardness, float damage, Random random) {
        float randomVariation = (0.5f + random.nextFloat() * 0.3f);
        return damage * randomVariation * this.penetrateChance > hardness;
    }

    private Vec3d calculateReflection(Vec3d incoming, Vec3d normal, Random random) {
        Vec3d reflected = incoming.subtract(normal.multiply(2 * incoming.dotProduct(normal)));

        float angle = random.nextFloat() * this.scatterAngle;
        return reflected.rotateX(angle * (random.nextBoolean() ? 1 : -1))
                .rotateY(angle * (random.nextBoolean() ? 1 : -1));
    }

    private void handleDamage(World world, Entity attacker, EntityHitResult hitResult, float damage) {
        Entity target = hitResult.getEntity();

        if (!(target instanceof LivingEntity living)) {
            target.damage(world.getDamageSources().create(this.damageType, attacker), damage);
            return;
        }

        living.hurtTime = 0;
        living.timeUntilRegen = 0;

        float percentageDamage = damage + (living.getMaxHealth() * this.healthBaseDamage);

        if (living instanceof PlayerEntity player) {
            // 简单部位判断
            HitBoxType hitBox = PreciseHitHelper.getHitBox(player, hitResult.getPos());

            float partDamageFactor = DAMAGE_MULTIPLIERS.getOrDefault(hitBox, 1.0f);
            float finalDamage = partDamageFactor * percentageDamage;

            player.damage(world.getDamageSources().create(this.damageType, attacker), finalDamage);
            return;
        }

        if (this.seriousInjury) {
            living.addStatusEffect(new StatusEffectInstance(
                    TPEffects.SERIOUS_INJURY, 200, 0, true, true
            ));
        }

        living.damage(world.getDamageSources().create(this.damageType, attacker), percentageDamage);
    }

    public static class Builder {
        private RegistryKey<DamageType> damageType = TPDamageTypes.FRAGMENT_HIT;
        private boolean showTrack = false;
        private boolean important = false;

        private float damage = 0.0f;
        private int maxHit = 3;
        private float entityDetectRadius = 0.3f;
        private float hardThreshold = 1.0f;
        private float bounceChance = 1.0f;
        private int baseRayLength = 32;
        private float bounceDamageLoss = 0.15f;
        private float scatterAngle = 0.1f;
        private float damageInfluence = 0.3f;
        private float bounceRandomVariation = 0.2f;
        private float penetrateChance = 1.0f;
        private float bouncePositionOffset = 0.1f;
        private float healthBaseDamage = 0.0f;
        private boolean seriousInjury = false;

        public TrajectoryRayCaster build() {
            return new TrajectoryRayCaster(this);
        }

        public TrajectoryRayCaster.Builder setDamageType(RegistryKey<DamageType> damageType) {
            this.damageType = damageType;
            return this;
        }

        public TrajectoryRayCaster.Builder showTrack(boolean showTrack) {
            this.showTrack = showTrack;
            return this;
        }

        public TrajectoryRayCaster.Builder isImportant(boolean bl) {
            this.important = bl;
            return this;
        }

        public TrajectoryRayCaster.Builder baseRayLength(int baseRayLength) {
            this.baseRayLength = Math.max(baseRayLength, 0);
            return this;
        }

        public TrajectoryRayCaster.Builder bounceHardThreshold(float hardThreshold) {
            this.hardThreshold = hardThreshold;
            return this;
        }

        public TrajectoryRayCaster.Builder maxHit(int maxHit) {
            this.maxHit = Math.max(maxHit, 0);
            return this;
        }

        public TrajectoryRayCaster.Builder baseDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public TrajectoryRayCaster.Builder bounceChance(float bounceChance) {
            this.bounceChance = MathHelper.clamp(bounceChance, 0.0f, 1.0f);
            return this;
        }

        public TrajectoryRayCaster.Builder entityDetectRadius(float f) {
            this.entityDetectRadius = f;
            return this;
        }

        public TrajectoryRayCaster.Builder bounceDamageLoss(float bounceDamageLoss) {
            this.bounceDamageLoss = bounceDamageLoss;
            return this;
        }

        public TrajectoryRayCaster.Builder bounceScatterAngle(float scatterAngle) {
            this.scatterAngle = scatterAngle;
            return this;
        }

        public TrajectoryRayCaster.Builder damageInfluence(float f) {
            this.damageInfluence = f;
            return this;
        }

        public TrajectoryRayCaster.Builder bounceRandomVariation(float f) {
            this.bounceRandomVariation = f;
            return this;
        }

        public TrajectoryRayCaster.Builder penetrateChance(float f) {
            this.penetrateChance = f;
            return this;
        }

        public TrajectoryRayCaster.Builder bouncePositionOffset(float offset) {
            this.bouncePositionOffset = offset;
            return this;
        }

        public TrajectoryRayCaster.Builder healthBaseDamage(float percentage) {
            this.healthBaseDamage = percentage;
            return this;
        }

        public TrajectoryRayCaster.Builder seriousInjury(boolean seriousInjury) {
            this.seriousInjury = seriousInjury;
            return this;
        }
    }
}
