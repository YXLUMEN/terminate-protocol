package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;

public class KraberItem extends WeaponItem {
    public KraberItem(Settings settings) {
        super(settings.maxDamage(12),
                new WeaponSettings(1800, 60, TPItems.SNIPER_AMMO)
                        .setRecoilType((short) 1)
                        .setAimFovMultiplier(0.1f)
                        .setRecoilDecayMultiplier(0.95f)
                        .setAimOffset(new Vec3d(-0.5115f, 0.0073f, -0.2f)),
                new TrajectoryRayCaster()
                        .showTrack(true)
                        .baseDamage(25)
                        .baseRayLength(160)
                        .bounceChance(0.2f)
                        .healthBaseDamage(0.3f));
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
    public WeaponStage getReloadStage(float reloadTick) {
        if (reloadTick == 0.55f) return WeaponStage.MAGIN;
        else if (reloadTick == 0.25f) return WeaponStage.BOLTBACK;
        else if (reloadTick == 0.0f) return WeaponStage.BOLTFORWARD;
        return null;
    }

    private static final Map<WeaponStage, List<SoundEvent>> sounds = Map.of(
            WeaponStage.FIRE, List.of(TPSoundEvents.KRABER_FIRE),
            WeaponStage.FIRE_LOW_AMMO, List.of(TPSoundEvents.KRABER_FIRE),
            WeaponStage.BOLTBACK, List.of(TPSoundEvents.KRABER_BOLTBACK_1, TPSoundEvents.KRABER_BOLTBACK_2, TPSoundEvents.KRABER_BOLTBACK_3),
            WeaponStage.BOLTFORWARD, List.of(TPSoundEvents.KRABER_BOLTFORWARD_1, TPSoundEvents.KRABER_BOLTFORWARD_2, TPSoundEvents.KRABER_BOLTFORWARD_3),
            WeaponStage.MAGIN, List.of(TPSoundEvents.KRABER_MAGIN),
            WeaponStage.MAGOUT, List.of(TPSoundEvents.KRABER_MAGOUT)
    );

    public SoundEvent getStageSound(WeaponStage stage) {
        if (stage == null) return null;
        List<SoundEvent> list = sounds.get(stage);
        if (list == null) return null;
        return list.get(this.random.nextInt(list.size()));
    }
}
