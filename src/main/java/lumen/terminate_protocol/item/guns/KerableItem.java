package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

public class KerableItem extends AbstractGunItem {
    private static final TrajectoryRayCaster KERABLE_RAY_CASTER = new TrajectoryRayCaster()
            .baseDamage(30)
            .baseRayLength(160)
            .bounceChance(0.2f);

    public KerableItem(Settings settings) {
        super(settings.maxDamage(12));
    }

    @Override
    public TrajectoryRayCaster getTrajectory() {
        return KERABLE_RAY_CASTER;
    }

    @Override
    public int getFireRant() {
        return 40;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 1.2f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 2.0f * (random.nextFloat() - 0.5f);
    }

    @Override
    public int getReloadTick() {
        return 60;
    }

    @Override
    public float getAimFOVMultiplier() {
        return 0.1f;
    }

    @Override
    protected SoundEvent getShootSound() {
        return SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE;
    }

    @Override
    public float getRecoilDecayFactor() {
        return 0.8f;
    }
}
