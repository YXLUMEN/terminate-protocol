package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class KraberItem extends AbstractWeaponItem implements IWeaponSound {
    private static final TrajectoryRayCaster KRABER_RAY_CASTER = new TrajectoryRayCaster()
            .showTrack(true)
            .baseDamage(25)
            .baseRayLength(160)
            .bounceChance(0.2f)
            .healthBaseDamage(0.3f);

    private static final Vec3d AIM_POS = new Vec3d(-0.5115f, 0.0073f, -0.2f);

    public KraberItem(Settings settings) {
        super(settings.maxDamage(12));
    }

    @Override
    public TrajectoryRayCaster getTrajectory() {
        return KRABER_RAY_CASTER;
    }

    @Override
    public int getFireRant() {
        return 1800;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 1.2f + random.nextFloat() * 0.3f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.3f + (random.nextFloat() - 0.5f) * 0.1f;
    }

    @Override
    public int getReloadTick() {
        return 60;
    }

    @Override
    public Item getAmmo() {
        return TPItems.SNIPER_AMMO;
    }

    @Override
    public float getAimFOVMultiplier() {
        return 0.1f;
    }

    @Override
    public float getRecoilDecayFactor() {
        return 0.95f;
    }

    @Override
    public short getRecoilType() {
        return 1;
    }

    @Override
    public Vec3d getAimPos() {
        return AIM_POS;
    }

    public SoundEvent getBoltbackSound(Random random) {
        return switch (random.nextBetween(0, 3)) {
            case 0 -> TPSoundEvents.KRABER_BOLTBACK_1;
            case 1 -> TPSoundEvents.KRABER_BOLTBACK_2;
            default -> TPSoundEvents.KRABER_BOLTBACK_3;
        };
    }

    public SoundEvent getBoltForwardSound(Random random) {
        return switch (random.nextBetween(0, 3)) {
            case 0 -> TPSoundEvents.KRABER_BOLTFORWARD_1;
            case 1 -> TPSoundEvents.KRABER_BOLTFORWARD_2;
            default -> TPSoundEvents.KRABER_BOLTFORWARD_3;
        };
    }

    @Override
    public SoundEvent getSounds(WeaponSoundStage part, Random random) {
        if (part == null) return null;
        return switch (part) {
            case FIRE, FIRE_LOW_AMMO -> TPSoundEvents.KRABER_FIRE;
            case BOLTBACK -> getBoltbackSound(random);
            case BOLTFORWARD -> getBoltForwardSound(random);
            case MAGIN -> TPSoundEvents.KRABER_MAGIN;
            case MAGOUT -> TPSoundEvents.KRABER_MAGOUT;
        };
    }

    @Override
    public WeaponSoundStage getReloadStage(float reloadTick) {
        if (reloadTick == 0.55f) return WeaponSoundStage.MAGIN;
        else if (reloadTick == 0.15f) return WeaponSoundStage.BOLTBACK;
        else if (reloadTick == 0.0f) return WeaponSoundStage.BOLTFORWARD;
        return null;
    }
}
