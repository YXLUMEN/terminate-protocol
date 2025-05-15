package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class R99Item extends AbstractWeaponItem implements IWeaponSound {
    private static final TrajectoryRayCaster R99_RAY_CASTER = new TrajectoryRayCaster()
            .baseDamage(2)
            .baseRayLength(32)
            .bounceChance(0.2f);

    public R99Item(Settings settings) {
        super(settings.maxDamage(21));
    }

    @Override
    protected TrajectoryRayCaster getTrajectory() {
        return R99_RAY_CASTER;
    }

    @Override
    public int getFireRant() {
        return 20;
    }

    @Override
    public int getReloadTick() {
        return 16;
    }

    @Override
    public Item getAmmo() {
        return TPItems.LIGHT_AMMO;
    }

    @Override
    public float getAimFOVMultiplier() {
        return 0.8f;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.4f + random.nextFloat() * 0.3f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.2f * (random.nextFloat() - 0.5f);
    }

    @Override
    public float getRecoilDecayFactor() {
        return 0.3f;
    }

    @Override
    public short getRecoilType() {
        return 0;
    }

    @Override
    public WeaponSoundStage getReloadStage(float reloadTick) {
        if (reloadTick == 0.75f) return WeaponSoundStage.MAGIN;
        else if (reloadTick == 0.25f) return WeaponSoundStage.BOLTBACK;
        else if (reloadTick == 0.0f) return WeaponSoundStage.BOLTFORWARD;
        return null;
    }

    SoundEvent getFireSound(Random random) {
        if (random.nextBetween(0, 1) == 0) {
            return TPSoundEvents.R99_FIRE_1;
        } else return TPSoundEvents.R99_FIRE_2;
    }

    SoundEvent getFireLowAmmoSound(Random random) {
        if (random.nextBetween(0, 1) == 0) {
            return TPSoundEvents.R99_FIRE_LOW_AMMO_1;
        } else return TPSoundEvents.R99_FIRE_LOW_AMMO_2;
    }

    @Override
    public SoundEvent getSounds(WeaponSoundStage part, Random random) {
        if (part == null) return null;
        return switch (part) {
            case FIRE -> getFireSound(random);
            case FIRE_LOW_AMMO -> getFireLowAmmoSound(random);
            case BOLTBACK -> TPSoundEvents.R99_BOLTBACK;
            case BOLTFORWARD -> TPSoundEvents.R99_BOLTFORWARD;
            case MAGIN -> TPSoundEvents.R99_MAGIN;
            case MAGOUT -> TPSoundEvents.R99_MAGOUT;
        };
    }
}
