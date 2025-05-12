package lumen.terminate_protocol.mixin.client;

import lumen.terminate_protocol.render.AimFovRender;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double originalFov = cir.getReturnValue();
        double modifiedFov = originalFov * AimFovRender.getFovMultiplier();
        cir.setReturnValue(modifiedFov);
    }
}
