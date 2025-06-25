package lumen.terminate_protocol.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SeriousInjury extends StatusEffect {
    public SeriousInjury() {
        super(StatusEffectCategory.HARMFUL, 0xA80000);
    }
}
