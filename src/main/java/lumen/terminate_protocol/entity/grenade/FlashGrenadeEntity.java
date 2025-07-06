package lumen.terminate_protocol.entity.grenade;

import lumen.terminate_protocol.api.TPDamageTypes;
import lumen.terminate_protocol.effect.TPEffects;
import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.network.packet.FlashEffectS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class FlashGrenadeEntity extends AbstractGrenadeEntity {
    private static final int DEFAULT_FUSE = 40;
    private static final short MAX_BOUNCES = 4;

    public static final short FLASH_RANGE = 16;
    public static final short SQ_FLAME_RANGE = FLASH_RANGE * FLASH_RANGE;
    public static final float MAX_ANGLE = 120f;

    public FlashGrenadeEntity(EntityType<? extends FlashGrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlashGrenadeEntity(World world, LivingEntity owner) {
        super(TPEntities.FLASH_GRENADE_ENTITY, world, owner);
    }

    public FlashGrenadeEntity(World world, double x, double y, double z) {
        super(TPEntities.FLASH_GRENADE_ENTITY, world, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return TPItems.FLASH_GRENADE;
    }

    @Override
    protected void explode() {
        if (this.getWorld().isClient) return;
        ServerWorld world = (ServerWorld) this.getWorld();

        Vec3d flashPos = this.getPos().add(0, this.getHeight() / 2, 0);

        world.spawnParticles(ParticleTypes.END_ROD, flashPos.x, flashPos.y + 1, flashPos.z,
                10, 0.2, 0.2, 0.2, 0.05);
        world.spawnParticles(ParticleTypes.FLASH, flashPos.x, flashPos.y + 0.5, flashPos.z,
                1, 0, 0, 0, 0);

        world.playSound(null, getBlockPos(),
                SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(null, getBlockPos(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.5f);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(FLASH_RANGE),
                e -> e.squaredDistanceTo(flashPos) <= SQ_FLAME_RANGE);

        for (LivingEntity entity : entities) {
            if (entity == null) continue;

            final float impact = calculateFlashStrength(world, this.getPos(), entity);
            if (impact <= 0.1f) continue;

            entity.damage(world.getDamageSources().create(TPDamageTypes.FLASH, this.getOwner()), 0.1f);

            if (entity instanceof ServerPlayerEntity player) {
                applyPlayerFlash(player, impact);
                continue;
            }

            entity.addStatusEffect(new StatusEffectInstance(
                    TPEffects.FLASHED, 200, 0, true, true
            ));
        }
    }

    @Override
    protected int getDefaultFuse() {
        return DEFAULT_FUSE;
    }

    @Override
    protected short getMaxBounces() {
        return MAX_BOUNCES;
    }

    private static boolean isBlocked(ServerWorld world, Vec3d flashPos, LivingEntity entity) {
        RaycastContext context = new RaycastContext(
                entity.getEyePos(), flashPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        );

        BlockHitResult ctx = world.raycast(context);
        return ctx.getType() == HitResult.Type.BLOCK && world.getBlockState(ctx.getBlockPos()).isOpaque();
    }

    private static float calculateSusceptibility(LivingEntity entity) {
        float factor = 1f;
        if (entity.isSubmergedInWater()) factor *= 0.8f;
        if (entity.hasStatusEffect(TPEffects.FLASHED)) factor *= 1.1f;
        if (entity.hasStatusEffect(StatusEffects.NIGHT_VISION)) factor *= 1.3f;
        if (entity.hasStatusEffect(StatusEffects.BLINDNESS)) factor *= 0.6f;
        return factor;
    }

    private static float calculateFlashStrength(ServerWorld world, Vec3d flashPos, LivingEntity entity) {
        // 计算距离因子
        float distanceFactor = (float) Math.pow(1 - entity.getPos().distanceTo(flashPos) / FLASH_RANGE, 0.4);

        // 计算角度因子
        float angleFactor;
        if (entity.isPlayer()) {
            Vec3d toFlash = flashPos.subtract(entity.getEyePos()).normalize();
            Vec3d lookVec = entity.getRotationVec(1.0f);
            float angle = (float) Math.toDegrees(Math.acos(lookVec.dotProduct(toFlash)));
            angleFactor = 1f - Math.min(1, angle / MAX_ANGLE);
            if (angleFactor == 0) return 0;
        } else {
            angleFactor = 1.0f;
        }

        // 墙体阻挡因子
        float blockFactor = isBlocked(world, flashPos, entity) ? 0.1f : 1f;

        // 状态因子
        float stateFactor = calculateSusceptibility(entity);

        return distanceFactor * angleFactor * blockFactor * stateFactor;
    }

    private static void applyPlayerFlash(ServerPlayerEntity player, float impact) {
        if (player == null || player.isSpectator()) return;
        float effectiveStrength = (float) Math.pow(impact, 1.5);
        int duration = 50 + (int) (150 * effectiveStrength);

        ServerPlayNetworking.send(player, new FlashEffectS2CPacket(effectiveStrength));
        player.addStatusEffect(new StatusEffectInstance(
                TPEffects.FLASHED, duration, 0, true, true
        ));
    }
}