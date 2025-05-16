package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.TPKeyBind;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.render.AimFovRender;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class ClientWeaponActionHandler {
    private static boolean wasFiring = false;
    private static boolean wasAiming = false;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            AimFovRender.updateFov(drawContext);
            WeaponRecoilSystem.updateRecoil();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            ItemStack stack = player.getMainHandStack();
            if (!(stack.getItem() instanceof WeaponItem item)) return;

            final boolean isFiring = client.options.attackKey.isPressed();
            final boolean isAiming = client.options.useKey.isPressed();

            if (isFiring != wasFiring) {
                if (!wasFiring) {
                    ClientFireHandler.onStartFire();
                } else {
                    ClientFireHandler.onEndFire(item);
                }
                wasFiring = isFiring;
            }
            if (isFiring) {
                ClientFireHandler.onFireTick(player, stack, isAiming);
            }

            if (isAiming != wasAiming) {
                wasAiming = isAiming;
                AimFovRender.setTargetFOVMultiplier(isAiming ? item.getSettings().getAimFOVMultiplier() : 1.0f);
            }

            if (TPKeyBind.WEAPON_RELOAD_KEY.isPressed()) {
                ClientReloadHandler.startReload(player, stack);
            }
        });
    }

    public static boolean getWasAiming() {
        return wasAiming;
    }
}
