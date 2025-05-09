package lumen.terminate_protocol.item;

import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;

public class VK47Item extends AbstractGunItem {
    private static final TrajectoryRayCaster VK47_RAY_CASTER = new TrajectoryRayCaster()
            .baseRayLength(64)
            .bounceChance(0)
            .penetrateChance(0.5f)
            .maxHit((short) 1);

    public VK47Item(Item.Settings settings) {
        super(settings.maxDamage(30));
    }

    @Override
    public TrajectoryRayCaster getRayCaster() {
        return VK47_RAY_CASTER;
    }

    @Override
    public float getFireDamage() {
        return 4.0f;
    }

    @Override
    public int getReloadConsume() {
        return 1;
    }

    @Override
    public int getFireRant() {
        return 3;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.8f * (0.9f + random.nextFloat());
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 1.8f * (random.nextFloat() - 0.3f);
    }

    @Override
    public int getReloadTick() {
        return 25;
    }
}
