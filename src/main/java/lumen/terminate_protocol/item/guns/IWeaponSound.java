package lumen.terminate_protocol.item.guns;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface IWeaponSound {
    @Nullable
    WeaponSoundStage getReloadStage(float reloadTick);

    @Nullable
    SoundEvent getSounds(@Nullable WeaponSoundStage part, Random random);
}
