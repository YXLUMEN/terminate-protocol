package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.damage_type.TPDamageTypes;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Map;

public class KraberItem extends WeaponItem implements IPullbolt {
    public KraberItem(Settings settings) {
        super(settings.maxDamage(6),
                new WeaponSettings(1800, 60, TPItems.SNIPER_AMMO, WeaponFireMode.BOLT)
                        .setAimFovMultiplier(0.1f)
                        .setRecoilDecayMultiplier(0.95f)
                        .setAimOffset(new Vec3d(-0.5112f, 0.0073f, -0.2f)),
                new TrajectoryRayCaster()
                        .showTrack(true)
                        .isImportant(true)
                        .baseDamage(30)
                        .baseRayLength(160)
                        .bounceChance(0.2f)
                        .healthBaseDamage(0.3f)
                        .setDamageType(TPDamageTypes.SNIPER_BULLET_HIT));
    }

    private static final Map<Integer, WeaponStage> reloadStages = Map.of(
            55, WeaponStage.MAGOUT,
            32, WeaponStage.MAGIN,
            11, WeaponStage.BOLTBACK,
            0, WeaponStage.BOLTFORWARD
    );

    private static final Map<WeaponStage, ISoundRecord> sounds = Map.of(
            WeaponStage.FIRE, SoundHelper.of(TPSoundEvents.KRABER_FIRE),
            WeaponStage.FIRE_LOW_AMMO, SoundHelper.of(TPSoundEvents.KRABER_FIRE),
            WeaponStage.BOLTBACK, SoundHelper.of(TPSoundEvents.KRABER_BOLTBACK),
            WeaponStage.BOLTFORWARD, SoundHelper.of(TPSoundEvents.KRABER_BOLTFORWARD),
            WeaponStage.MAGIN, SoundHelper.of(TPSoundEvents.KRABER_MAGIN),
            WeaponStage.MAGOUT, SoundHelper.of(TPSoundEvents.KRABER_MAGOUT)
    );

    @Override
    public float getVerticalRecoil(Random random) {
        return 1.2f + random.nextFloat() * 0.3f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.3f + (random.nextFloat() - 0.5f) * 0.1f;
    }

    @Override
    public int getPullboltTick() {
        return 25;
    }

    @Override
    public WeaponStage getReloadStageFromTick(int reloadTick) {
        return reloadStages.get(reloadTick);
    }

    public ISoundRecord getStageSound(WeaponStage stage) {
        if (stage == null) return null;
        return sounds.get(stage);
    }
}
