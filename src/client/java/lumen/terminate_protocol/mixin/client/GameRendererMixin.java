package lumen.terminate_protocol.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lumen.terminate_protocol.render.AimFovRender;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameRenderer.class, priority = 800)
public abstract class GameRendererMixin {
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private double modifyFov(double originalFov, Camera camera, float tickDelta, boolean changingFov) {
        return originalFov * AimFovRender.getFovMultiplier();
    }
}
