package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.TPDamageTypes;
import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.ISoundRecord;
import lumen.terminate_protocol.util.SoundHelper;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Map;

public class R99Item extends WeaponItem {
    public R99Item(Settings settings) {
        super(settings.maxDamage(46),
                new WeaponSettings(0, 36, TPItems.LIGHT_AMMO, WeaponFireMode.FULL_AUTOMATIC)
                        .setAimOffset(new Vec3d(-0.514f, 0.15, 0))
                        .setAimFovMultiplier(0.8f)
                        .setRecoilDecayMultiplier(0.3f),
                new TrajectoryRayCaster()
                        .showTrack(true)
                        .baseDamage(3)
                        .baseRayLength(50)
                        .penetrateChance(0.2f)
                        .bounceChance(0.4f)
                        .setDamageType(TPDamageTypes.LIGHT_BULLET_HIT));
    }

    private static final Map<Integer, WeaponStage> reloadStages = Map.of(
            34, WeaponStage.MAGOUT,
            20, WeaponStage.MAGIN,
            8, WeaponStage.BOLTBACK,
            0, WeaponStage.BOLTFORWARD
    );

    private static final Map<WeaponStage, ISoundRecord> sounds = Map.of(
            WeaponStage.FIRE, SoundHelper.of(TPSoundEvents.R99_FIRE),
            WeaponStage.BOLTBACK, SoundHelper.of(TPSoundEvents.R99_BOLTBACK),
            WeaponStage.BOLTFORWARD, SoundHelper.of(TPSoundEvents.R99_BOLTFORWARD),
            WeaponStage.MAGIN, SoundHelper.of(TPSoundEvents.R99_MAGIN),
            WeaponStage.MAGOUT, SoundHelper.of(TPSoundEvents.R99_MAGOUT)
    );

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.4f + random.nextFloat() * 0.3f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.2f * (random.nextFloat() - 0.5f);
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
            if (lowAmmo >= 0.7f) return SoundHelper.builder()
                    .add(TPSoundEvents.R99_FIRE_LOW_AMMO, lowAmmo)
                    .add(TPSoundEvents.R99_FIRE, 0.4f).build();
        }
        return sounds.get(stage);
    }
}
