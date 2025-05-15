package lumen.terminate_protocol.mixin.client;

import lumen.terminate_protocol.item.guns.AbstractWeaponItem;
import lumen.terminate_protocol.weapon_handler.ClientWeaponActionHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (ClientWeaponActionHandler.getWasAiming() && item.getItem() instanceof AbstractWeaponItem item1) {
            matrices.push();

            Vec3d pos = item1.getAimPos();
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.scale(1.5f, 1.5f, 1.5f);
        }
    }
}