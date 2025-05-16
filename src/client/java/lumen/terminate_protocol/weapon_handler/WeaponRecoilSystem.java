package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.item.weapon.WeaponItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class WeaponRecoilSystem {
    private static final float BASE_RECOIL_DECAY = 0.6f;
    private static final float MAX_VERTICAL_RECOIL = 15f;
    private static final float MAX_HORIZONTAL_RECOIL = 5f;

    private static float currentVerticalRecoil = 0f;
    private static float currentHorizontalRecoil = 0f;

    private static float recoveryVerticalRecoil = 0f;
    private static float recoveryHorizontalRecoil = 0f;

    public static void applyRecoil(float verticalRecoil, float horizontalRecoil) {
        currentVerticalRecoil = MathHelper.clamp(currentVerticalRecoil + verticalRecoil, 0, MAX_VERTICAL_RECOIL);
        currentHorizontalRecoil = MathHelper.clamp(currentHorizontalRecoil + horizontalRecoil, -MAX_HORIZONTAL_RECOIL, MAX_HORIZONTAL_RECOIL);
    }

    public static void applyRecovery(float verticalRecoil, float horizontalRecoil) {
        recoveryVerticalRecoil = MathHelper.clamp(currentVerticalRecoil - verticalRecoil, -MAX_VERTICAL_RECOIL, MAX_VERTICAL_RECOIL);
        recoveryHorizontalRecoil = MathHelper.clamp(currentHorizontalRecoil - horizontalRecoil, -MAX_HORIZONTAL_RECOIL, MAX_HORIZONTAL_RECOIL);
    }

    public static void updateRecoil() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        updateRecovery();
        if (currentVerticalRecoil == 0 && currentHorizontalRecoil == 0) return;

        player.setPitch(player.getPitch() - currentVerticalRecoil);
        player.setYaw(player.getYaw() + currentHorizontalRecoil);

        if (player.getMainHandStack().getItem() instanceof WeaponItem item) {
            currentVerticalRecoil *= item.getSettings().getRecoilDepthMultiplier();
            currentHorizontalRecoil *= item.getSettings().getRecoilDepthMultiplier();
        } else {
            currentVerticalRecoil *= BASE_RECOIL_DECAY;
            currentHorizontalRecoil *= BASE_RECOIL_DECAY;
        }

        if (Math.abs(currentVerticalRecoil) < 0.01f) currentVerticalRecoil = 0f;
        if (Math.abs(currentHorizontalRecoil) < 0.01f) currentHorizontalRecoil = 0f;
    }

    private static void updateRecovery() {
        if (recoveryHorizontalRecoil == 0 && recoveryVerticalRecoil == 0) return;

        currentVerticalRecoil = recoveryVerticalRecoil;
        currentHorizontalRecoil = recoveryHorizontalRecoil;
        recoveryVerticalRecoil = 0;
        recoveryHorizontalRecoil = 0;
    }
}
