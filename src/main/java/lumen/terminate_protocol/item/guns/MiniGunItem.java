package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.util.math.random.Random;

public class MiniGunItem extends AbstractGunItem {
    private static final TrajectoryRayCaster MINI_GUN_RAY_CASTER = new TrajectoryRayCaster()
            .baseDamage(3.5f)
            .baseRayLength(64)
            .bounceChance(0.1f)
            .penetrateChance(0.5f);

    public MiniGunItem(Settings settings) {
        super(settings.maxDamage(173));
    }

    @Override
    public TrajectoryRayCaster getTrajectory() {
        return MINI_GUN_RAY_CASTER;
    }

    @Override
    public int getFireRant() {
        return 1;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.5f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0;
    }

    @Override
    public int getReloadTick() {
        return 40;
    }

    @Override
    public float getAimFOVMultiplier() {
        return 0.8f;
    }

    @Override
    public float getRecoilDecayFactor() {
        return 0.4f;
    }
}
