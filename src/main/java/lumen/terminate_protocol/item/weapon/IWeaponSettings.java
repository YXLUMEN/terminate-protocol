package lumen.terminate_protocol.item.weapon;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface IWeaponSettings {
    float getVerticalRecoil(Random random);

    float getHorizontalRecoil(Random random);

    @Nullable
    SoundEvent getStageSound(@Nullable WeaponStage stage);

    WeaponStage getReloadStage(float reloadProgress);
}
