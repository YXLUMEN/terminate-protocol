package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.TPDamageTypes;
import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.client.render.item.SpiteFireItemRender;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

public class SpitfireItem extends WeaponItem implements GeoItem {
    private static final RawAnimation FIRE_ANIM = RawAnimation.begin().thenPlay("animation.spitfire.fire");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SpitfireItem(Settings settings) {
        super(settings.maxDamage(80),
                new WeaponSettings(2, 65, TPItems.LIGHT_AMMO, WeaponFireMode.FULL_AUTOMATIC)
                        .setAimOffset(new Vec3d(-0.513f, 0.05f, 0))
                        .setAimFovMultiplier(0.7f)
                        .setRecoilDecayMultiplier(0.4f),
                new TrajectoryRayCaster()
                        .showTrack(true)
                        .baseDamage(5)
                        .baseRayLength(100)
                        .penetrateChance(0.5f)
                        .bounceChance(0.2f)
                        .setDamageType(TPDamageTypes.HEAVY_BULLET_HIT));

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static final Map<Integer, WeaponStage> reloadStages = Map.of(
            60, WeaponStage.MAGOUT,
            33, WeaponStage.MAGIN,
            19, WeaponStage.MAGPAT,
            8, WeaponStage.BOLTBACK,
            0, WeaponStage.BOLTFORWARD
    );

    private static final Map<WeaponStage, ISoundRecord> sounds = Map.of(
            WeaponStage.FIRE, SoundHelper.builder().add(TPSoundEvents.SPITFIRE_FIRE, 1.0f).add(TPSoundEvents.SPITFIRE_FIRE_MECH, 0.6f).build(),
            WeaponStage.FIRE_LOW_AMMO, SoundHelper.of(TPSoundEvents.SPITFIRE_FIRE_MECH),
            WeaponStage.BOLTBACK, SoundHelper.of(TPSoundEvents.SPITFIRE_BOLTBACK),
            WeaponStage.BOLTFORWARD, SoundHelper.of(TPSoundEvents.SPITFIRE_BOLTFORWARD),
            WeaponStage.MAGIN, SoundHelper.of(TPSoundEvents.SPITFIRE_MAGIN),
            WeaponStage.MAGPAT, SoundHelper.of(TPSoundEvents.SPITFIRE_MAGPAT),
            WeaponStage.MAGOUT, SoundHelper.of(TPSoundEvents.SPITFIRE_MAGOUT)
    );

    @Override
    public boolean onFire(World world, PlayerEntity player, ItemStack stack) {
        boolean bl = super.onFire(world, player, stack);
        if (bl) {
            this.triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerWorld) world), "Controller", "animation.spitfire.fire");
        }
        return bl;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.5f + random.nextFloat() * 0.3f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.4f * (random.nextFloat() - 0.5f);
    }

    @Override
    public WeaponStage getReloadStageFromTick(int reloadTick) {
        return reloadStages.get(reloadTick);
    }

    @Override
    public ISoundRecord getStageSound(WeaponStage stage, ItemStack stack) {
        if (stage == null) return null;
        if (stage == WeaponStage.FIRE) {
            float lowAmmo = (float) stack.getDamage() / stack.getMaxDamage();
            if (lowAmmo >= 0.95) return sounds.get(WeaponStage.FIRE_LOW_AMMO);
            if (lowAmmo >= 0.65f) return SoundHelper.builder()
                    .add(TPSoundEvents.SPITFIRE_FIRE, 0.8f)
                    .add(TPSoundEvents.SPITFIRE_FIRE_MECH, lowAmmo).build();
        }
        return sounds.get(stage);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private SpiteFireItemRender renderer;

            @Override
            public SpiteFireItemRender getGeoItemRenderer() {
                if (this.renderer == null) this.renderer = new SpiteFireItemRender();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", state -> PlayState.STOP)
                .triggerableAnim("animation.spitfire.fire", FIRE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
