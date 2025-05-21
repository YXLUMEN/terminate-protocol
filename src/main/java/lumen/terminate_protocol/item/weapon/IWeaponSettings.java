package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.util.ISoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface IWeaponSettings {
    float getVerticalRecoil(Random random);

    float getHorizontalRecoil(Random random);

    @Nullable
    ISoundRecord getStageSound(@Nullable WeaponStage stage, ItemStack stack);

    @Nullable
    WeaponStage getReloadStageFromTick(int reloadTick);
}
