package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.util.ISoundRecord;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface IWeaponSettings {
    float getVerticalRecoil(Random random);

    float getHorizontalRecoil(Random random);

    @Nullable
    ISoundRecord getStageSound(@Nullable WeaponStage stage);

    @Nullable
    WeaponStage getReloadStageFromTick(int reloadTick);
}
