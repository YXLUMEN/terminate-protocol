package lumen.terminate_protocol.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static lumen.terminate_protocol.weapon_handler.ClientWeaponActionHandler.getIsHeldWeapon;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultAttack(CallbackInfoReturnable<Boolean> cir) {
        if (getIsHeldWeapon()) cir.setReturnValue(false);
    }
}