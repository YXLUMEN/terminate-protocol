package lumen.terminate_protocol.item.guns;

import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;

public interface IWeaponSettings {
    short getRecoilType();

    int getFireRant();

    int getReloadTick();

    Item getAmmo();

    float getAimFOVMultiplier();

    float getVerticalRecoil(Random random);

    float getHorizontalRecoil(Random random);

    float getRecoilDecayFactor();
}
