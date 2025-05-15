package lumen.terminate_protocol.item.guns;

import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.util.TrajectoryRayCaster;
import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;

public class VK47Item extends AbstractWeaponItem {
    private static final TrajectoryRayCaster VK47_RAY_CASTER = new TrajectoryRayCaster()
            .baseDamage(4.0f)
            .baseRayLength(64)
            .bounceChance(0.2f)
            .penetrateChance(0.5f);

    public VK47Item(Item.Settings settings) {
        super(settings.maxDamage(30));
    }

    @Override
    public TrajectoryRayCaster getTrajectory() {
        return VK47_RAY_CASTER;
    }

    @Override
    public int getFireRant() {
        return 150;
    }

    @Override
    public float getVerticalRecoil(Random random) {
        return 0.1f;
    }

    @Override
    public float getHorizontalRecoil(Random random) {
        return 2.0f * (random.nextFloat() - 0.5f);
    }

    @Override
    public int getReloadTick() {
        return 25;
    }

    @Override
    public float getAimFOVMultiplier() {
        return 0.6f;
    }

    @Override
    public float getRecoilDecayFactor() {
        return 0.5f;
    }

    @Override
    public Item getAmmo() {
        return TPItems.HEAVY_AMMO;
    }

    @Override
    public short getRecoilType() {
        return 0;
    }
}
