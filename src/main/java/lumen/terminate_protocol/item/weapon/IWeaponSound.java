package lumen.terminate_protocol.item.weapon;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface IWeaponSound {
    @Nullable
    WeaponStage getReloadStage(float reloadTick);

    @Nullable
    SoundEvent getSounds(@Nullable WeaponStage part, Random random);
}
