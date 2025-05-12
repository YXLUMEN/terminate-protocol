package lumen.terminate_protocol.firearm_handler;

import lumen.terminate_protocol.TPKeyBind;
import lumen.terminate_protocol.item.guns.AbstractGunItem;
import lumen.terminate_protocol.render.AimFovRender;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class GunActiveHandler {
    private static boolean wasAiming = false;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            AimFovRender.updateFov(drawContext);
            GunRecoilSystem.updateRecoil();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            ItemStack itemStack = player.getMainHandStack();
            if (!(itemStack.getItem() instanceof AbstractGunItem item)) return;

            final boolean isAiming = client.options.useKey.isPressed();
            if (client.options.attackKey.isPressed()) {
                ClientFireHandler.onFireTick(player, itemStack, isAiming);
            }

            if (isAiming != wasAiming) {
                wasAiming = isAiming;
                AimFovRender.setTargetFOVMultiplier(isAiming ? item.getAimFOVMultiplier() : 1.0f);
            }

            if (TPKeyBind.RELOAD_GUN_KEY.isPressed()) {
                ClientReloadHandler.startReload(player, itemStack);
            }
        });
    }
}
