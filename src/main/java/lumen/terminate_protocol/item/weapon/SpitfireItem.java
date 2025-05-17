package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class SpitfireItem extends WeaponItem {
    public SpitfireItem(Settings settings) {
        super(settings.maxDamage(80),
                new WeaponSettings(80, 60, TPItems.LIGHT_AMMO)
                        .setAimOffset(new Vec3d(-0.514f, -0.1, 0))
                        .setAimFovMultiplier(0.7f)
                        .setRecoilDecayMultiplier(0.4f),
                new TrajectoryRayCaster()
                        .baseDamage(4)
                        .baseRayLength(32)
                        .penetrateChance(0.5f)
                        .bounceChance(0.2f));
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
    public @Nullable SoundEvent getStageSound(@Nullable WeaponStage stage) {
        return null;
    }

    @Override
    public WeaponStage getReloadStage(float reloadProgress) {
        return null;
    }
}
