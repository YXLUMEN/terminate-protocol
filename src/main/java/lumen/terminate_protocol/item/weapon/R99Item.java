package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;

public class R99Item extends WeaponItem {
    public R99Item(Settings settings) {
        super(settings.maxDamage(21),
                new WeaponSettings(20, 24, TPItems.LIGHT_AMMO)
                        .setAimOffset(new Vec3d(-0.514f, 0.15, 0))
                        .setAimFovMultiplier(0.8f)
                        .setRecoilDecayMultiplier(0.3f),
                new TrajectoryRayCaster()
                        .baseDamage(2)
                        .baseRayLength(32)
                        .bounceChance(0.2f));
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
    public WeaponStage getReloadStage(float reloadTick) {
        if (reloadTick == 0.625f) return WeaponStage.MAGIN;
        else if (reloadTick == 0.25f) return WeaponStage.BOLTBACK;
        else if (reloadTick <= 0.0f) return WeaponStage.BOLTFORWARD;
        return null;
    }

    private static final Map<WeaponStage, List<SoundEvent>> sounds = Map.of(
            WeaponStage.FIRE, List.of(TPSoundEvents.R99_FIRE_1, TPSoundEvents.R99_FIRE_2),
            WeaponStage.FIRE_LOW_AMMO, List.of(TPSoundEvents.R99_FIRE_LOW_AMMO_1, TPSoundEvents.R99_FIRE_LOW_AMMO_2),
            WeaponStage.BOLTBACK, List.of(TPSoundEvents.R99_BOLTBACK),
            WeaponStage.BOLTFORWARD, List.of(TPSoundEvents.R99_BOLTFORWARD),
            WeaponStage.MAGIN, List.of(TPSoundEvents.R99_MAGIN),
            WeaponStage.MAGOUT, List.of(TPSoundEvents.R99_MAGOUT)
    );

    @Override
    public SoundEvent getStageSound(WeaponStage stage) {
        if (stage == null) return null;
        List<SoundEvent> list = sounds.get(stage);
        if (list == null) return null;
        return list.get(this.random.nextInt(list.size()));
    }
}
