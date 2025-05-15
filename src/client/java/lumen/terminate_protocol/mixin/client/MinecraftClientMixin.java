package lumen.terminate_protocol.mixin.client;

import lumen.terminate_protocol.item.guns.AbstractWeaponItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void cancelDefaultAttack(CallbackInfoReturnable<Boolean> cir) {
        if (shouldCancel()) cir.setReturnValue(false);
    }

    @Unique
    private boolean shouldCancel() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        return player.getMainHandStack().getItem() instanceof AbstractWeaponItem;
    }
}