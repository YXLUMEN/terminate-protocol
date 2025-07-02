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
    private static final float ENTITY_DETECT_RADIUS = 0.3f;
    private RegistryKey<DamageType> damageType = TPDamageTypes.FRAGMENT_HIT;
    private boolean showTrack = false;
    private boolean important = false;

    private float damage = 0.0f;
    private short maxHit = 2;
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

    public TrajectoryRayCaster() {
    }

    public TrajectoryRayCaster setDamageType(RegistryKey<DamageType> damageType) {
        this.damageType = damageType;
        return this;
    }

    public TrajectoryRayCaster showTrack(boolean showTrack) {
        this.showTrack = showTrack;
        return this;
    }

    public TrajectoryRayCaster isImportant(boolean bl) {
        this.important = bl;
        return this;
    }

    public TrajectoryRayCaster baseRayLength(int baseRayLength) {
        this.baseRayLength = baseRayLength;
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

    public TrajectoryRayCaster baseDamage(float damage) {
        this.damage = damage;
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

    public TrajectoryRayCaster bouncePositionOffset(float offset) {
        this.bouncePositionOffset = offset;
        return this;
    }

    public TrajectoryRayCaster healthBaseDamage(float percentage) {
        this.healthBaseDamage = percentage;
        return this;
    }

    public TrajectoryRayCaster seriousInjury(boolean seriousInjury) {
        this.seriousInjury = seriousInjury;
        return this;
    }

    public void start(World world, Entity attacker, Vec3d start, Vec3d dir) {
        Random random = world.random;

        Vec3d currentPos = new Vec3d(start.x, start.y, start.z);
        Vec3d currentDir = dir.normalize();

        float remainingDamage = damage;
        int remainingHit = maxHit;

        while (remainingHit-- > 0 && remainingDamage > 0.5f) {
            Vec3d endPos = currentPos.add(currentDir.multiply(baseRayLength));

            EntityHitResult entityHit = ProjectileUtil.raycast(
                    attacker, currentPos, endPos,
                    new Box(currentPos, endPos).expand(ENTITY_DETECT_RADIUS),
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
                    if (showTrack) syncTrack(attacker, currentPos, endPos, important);
                    return;
                }

                handleDamage(world, attacker, entityHit, remainingDamage);
                if (showTrack) syncTrack(attacker, currentPos, entityHit.getPos(), important);
                return;
            }

            if (entityHit != null && currentPos.squaredDistanceTo(blockHit.getPos()) > currentPos.squaredDistanceTo(entityHit.getPos())) {
                handleDamage(world, attacker, entityHit, remainingDamage);
                if (showTrack) syncTrack(attacker, currentPos, entityHit.getPos(), important);
                return;
            }

            BlockState blockState = world.getBlockState(blockHit.getBlockPos());
            final float originHardness = blockState.getHardness(world, blockHit.getBlockPos());
            final float hardness = originHardness > 0 ? originHardness : 100.0f;

            Direction face = blockHit.getSide();
            final Vec3d normal = new Vec3d(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ()).normalize();

            if (showTrack) syncTrack(attacker, currentPos, blockHit.getPos(), important);
            if (shouldBounce(currentDir, normal, hardness, remainingDamage, random)) {
                currentDir = calculateReflection(currentDir, normal, random).normalize();
                currentPos = blockHit.getPos().add(currentDir.multiply(bouncePositionOffset));
                remainingDamage *= (1 - bounceDamageLoss);

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                            currentPos.x, currentPos.y, currentPos.z, 1, 0, 0, 0, 0);
                }

            } else if (tryPenetrate(hardness, remainingDamage, random)) {
                // 穿透
                currentPos = blockHit.getPos().add(currentDir);
                remainingDamage *= Math.max(0.3f, 1 - (hardness * 0.2f));
                if (entityHit != null) handleDamage(world, attacker, entityHit, remainingDamage);

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

        float hardnessFactor = Math.min(1, hardness / hardThreshold);

        float damageFactor = 1 / (1 + damageInfluence * damage);

        float randomVariation = 1 + (random.nextFloat() * 2 - 1) * bounceRandomVariation;

        float bounceProbability = MathHelper.clamp(
                (0.5f * hardnessFactor + 0.4f * angleFactor + 0.1f * damageFactor) * randomVariation * bounceChance,
                0.1f, 0.8f);

        return random.nextFloat() < bounceProbability;
    }

    private boolean tryPenetrate(float hardness, float damage, Random random) {
        float randomVariation = (0.5f + random.nextFloat() * 0.3f);
        return damage * randomVariation * penetrateChance > hardness;
    }

    private Vec3d calculateReflection(Vec3d incoming, Vec3d normal, Random random) {
        Vec3d reflected = incoming.subtract(normal.multiply(2 * incoming.dotProduct(normal)));

        float angle = random.nextFloat() * scatterAngle;
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
}
