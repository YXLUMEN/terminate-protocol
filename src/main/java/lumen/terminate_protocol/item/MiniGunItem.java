package lumen.terminate_protocol.item;

import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.util.math.random.Random;

public class MiniGunItem extends AbstractGunItem {
    private static final TrajectoryRayCaster MINI_GUN_RAY_CASTER = new TrajectoryRayCaster()
            .baseRayLength(64)
            .bounceChance(0)
            .penetrateChance(0.1f)
            .maxHit((short) 1);

    public MiniGunItem(Settings settings) {
        super(settings.maxDamage(200));
    }

    @Override
    public TrajectoryRayCaster getRayCaster() {
        return MINI_GUN_RAY_CASTER;
    }

    @Override
    public float getFireDamage() {
        return 3.5f;
    }

    @Override
    public int getReloadConsume() {
        return 1;
    }

    @Override
    public int getFireRant() {
        return 1;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.6f * (0.9f + random.nextFloat());
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 0.1f * (random.nextFloat() - 0.5f);
    }

    @Override
    public int getReloadTick() {
        return 40;
    }
}
