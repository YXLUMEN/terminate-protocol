package lumen.terminate_protocol.mixin.client;

import lumen.terminate_protocol.render.AimFovRender;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameOptions.class, priority = 800)
public abstract class GameOptionsMixin {
    @Unique
    private double originValue = 0;

    @Inject(method = "getMouseSensitivity", at = @At("RETURN"), cancellable = true)
    private void onGetMouseSensitivity(CallbackInfoReturnable<SimpleOption<Double>> cir) {
        if (AimFovRender.getFovMultiplier() <= 0.3f) {
            var originSensitivity = cir.getReturnValue();
            if (originValue == 0) originValue = originSensitivity.getValue();
            originSensitivity.setValue(originValue * AimFovRender.getFovMultiplier());
            cir.setReturnValue(originSensitivity);
            return;
        }

        if (originValue != 0) {
            var originSensitivity = cir.getReturnValue();
            originSensitivity.setValue(originValue);
            originValue = 0;
            cir.setReturnValue(originSensitivity);
        }
    }
}
