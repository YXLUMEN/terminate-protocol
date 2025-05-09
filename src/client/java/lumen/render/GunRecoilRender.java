package lumen.render;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class GunRecoilRender {
    private static final float RECOIL_DECAY = 0.5f;
    private static final float MAX_VERTICAL_RECOIL = 15f;
    private static final float MAX_HORIZONTAL_RECOIL = 5f;

    private float currentVerticalRecoil = 0f;
    private float currentHorizontalRecoil = 0f;

    public void applyRecoil(float verticalRecoil, float horizontalRecoil) {
        currentVerticalRecoil += verticalRecoil;
        currentHorizontalRecoil += horizontalRecoil;

        currentVerticalRecoil = MathHelper.clamp(currentVerticalRecoil, 0, MAX_VERTICAL_RECOIL);
        currentHorizontalRecoil = MathHelper.clamp(currentHorizontalRecoil, -MAX_HORIZONTAL_RECOIL, MAX_HORIZONTAL_RECOIL);
    }

    public void update(ClientPlayerEntity player) {
        if (player == null) return;

        if (currentVerticalRecoil != 0 || currentHorizontalRecoil != 0) {
            player.setPitch(player.getPitch() - currentVerticalRecoil);
            player.setYaw(player.getYaw() + currentHorizontalRecoil);

            currentVerticalRecoil *= RECOIL_DECAY;
            currentHorizontalRecoil *= RECOIL_DECAY;

            if (Math.abs(currentVerticalRecoil) < 0.01f) currentVerticalRecoil = 0f;
            if (Math.abs(currentHorizontalRecoil) < 0.01f) currentHorizontalRecoil = 0f;
        }
    }
}
