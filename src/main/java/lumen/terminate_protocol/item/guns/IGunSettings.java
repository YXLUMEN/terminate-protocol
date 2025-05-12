package lumen.terminate_protocol.item.guns;

import net.minecraft.util.math.random.Random;

public interface IGunSettings {
    int getFireRant();

    int getReloadTick();

    float getAimFOVMultiplier();

    float getVerticalRecoil(Random random);

    float getHorizontalRecoil(Random random);

    float getRecoilDecayFactor();
}
