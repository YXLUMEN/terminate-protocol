package lumen.terminate_protocol.componment.sound;

import lumen.terminate_protocol.util.SoundHelper;
import net.minecraft.item.ItemStack;

public interface IFireSound {
    SoundHelper.SingleSound getSound(ItemStack stack);
}
