package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.client.weapon_handler.ClientWeaponActionHandler;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
            shift = At.Shift.AFTER))
    private void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(item.getItem() instanceof WeaponItem wItem)) return;

        boolean aiming = ClientWeaponActionHandler.getWasAiming();

        if (!aiming && (item.getOrDefault(TPComponentTypes.WPN_RELOADING, false) || player.isSprinting())) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(60));
            return;
        }

        if (aiming) {
            Vec3d pos = wItem.getSettings().getAimOffset();

            matrices.translate(pos.x, pos.y, pos.z);
            matrices.scale(1.5f, 1.5f, 1.5f);
        }
    }
}