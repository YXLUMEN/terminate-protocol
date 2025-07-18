package lumen.terminate_protocol.client.weapon_handler;


import lumen.terminate_protocol.api.WeaponFireMode;
import lumen.terminate_protocol.api.WeaponStage;
import lumen.terminate_protocol.client.TPKeyBind;
import lumen.terminate_protocol.client.render.AimFovRender;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.util.ISoundRecord;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import static lumen.terminate_protocol.client.weapon_handler.ClientAimHandler.setAim;
import static lumen.terminate_protocol.client.weapon_handler.ClientFireHandler.*;
import static lumen.terminate_protocol.client.weapon_handler.ClientReloadHandler.startReload;


public class ClientWeaponActionHandler {
    private static boolean isHeldWeapon = false;
    private static boolean isPullbolt = false;
    private static boolean wasFiring = false;
    private static boolean wasAiming = false;

    private static void weaponActionHandler(MinecraftClient client) {
        reloadManager.update();

        ClientPlayerEntity player = client.player;
        if (player == null) {
            isHeldWeapon = false;
            return;
        }

        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof WeaponItem item)) {
            isHeldWeapon = false;
            setAim(false, 1.0f);
            return;
        }
        isHeldWeapon = true;

        WeaponFireMode fireMode = item.getSettings().getFireMode();

        // 拉栓
        if (fireMode != WeaponFireMode.FULL_AUTOMATIC) {
            int pullbolt = reloadManager.getCooldownTicks(item);
            if (isPullbolt) {
                player.setSprinting(false);
                WeaponStage stage = item.getReloadStageFromTick(pullbolt);
                ISoundRecord record = item.getStageSound(stage, stack);
                if (record != null) clientPlaySoundRecord(record, player);
            }
            isPullbolt = pullbolt > 0;
        } else isPullbolt = false;

        // 按键控制
        final boolean isFiring = client.options.attackKey.isPressed();
        final boolean isAiming = client.options.useKey.isPressed();

        if (isFiring != wasFiring) {
            if (!wasFiring) {
                doStartFire();
            } else {
                doEndFire(fireMode);
            }
            wasFiring = isFiring;
        }

        if (isFiring) {
            player.setSprinting(false);
            doFireTick(player, stack, item);
        }

        if (isAiming != wasAiming) {
            wasAiming = isAiming;
            player.setSprinting(false);
            setAim(isAiming, isAiming ? item.getSettings().getAimFOVMultiplier() : 1.0f);
        }

        if (TPKeyBind.RELOAD_KEY_BINDING.isPressed()) {
            startReload(player, stack);
        }
    }

    public static boolean getIsHeldWeapon() {
        return isHeldWeapon;
    }

    public static boolean getWasAiming() {
        return wasAiming;
    }

    public static boolean getIsPullbolt() {
        return isPullbolt;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            AimFovRender.updateFov();
            ClientWeaponRecoilSystem.updateRecoil();
        });

        ClientTickEvents.END_CLIENT_TICK.register(ClientWeaponActionHandler::weaponActionHandler);
    }
}
