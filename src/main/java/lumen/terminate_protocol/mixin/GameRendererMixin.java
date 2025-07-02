package lumen.terminate_protocol.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lumen.terminate_protocol.client.render.AimFovRender;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static lumen.terminate_protocol.client.weapon_handler.ClientWeaponActionHandler.getWasAiming;

@Mixin(value = GameRenderer.class, priority = 800)
public abstract class GameRendererMixin {
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private double modifyFov(double originalFov, Camera camera, float tickDelta, boolean changingFov) {
        return originalFov * AimFovRender.getFovMultiplier();
    }

    @Inject(
            method = "bobView",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 1), cancellable = true)
    private void cancelPop(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (getWasAiming()) ci.cancel();
    }
}
