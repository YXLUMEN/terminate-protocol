package lumen.terminate_protocol.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AimFovRender {
    private static final Identifier SPYGLASS_SCOPE = Identifier.ofVanilla("textures/misc/spyglass_scope.png");
    private static final float TRANSITION_SPEED = 0.3f;
    private static float targetFovMultiplier = 1.0f;
    private static float currentFovMultiplier = 1.0f;

    public static void updateFov(DrawContext drawContext) {
        if (targetFovMultiplier <= 0.3f) {
            renderSpyglassOverlay(drawContext);
        }

        if (currentFovMultiplier == targetFovMultiplier) return;
        float diff = targetFovMultiplier - currentFovMultiplier;
        if (Math.abs(diff) > 0.001f) {
            currentFovMultiplier += diff * TRANSITION_SPEED;
        } else {
            currentFovMultiplier = targetFovMultiplier;
        }
    }

    public static void setTargetFOVMultiplier(float fov) {
        targetFovMultiplier = fov;
    }

    public static float getFovMultiplier() {
        return currentFovMultiplier;
    }

    private static void renderSpyglassOverlay(DrawContext context) {
        float f = Math.min(context.getScaledWindowWidth(), context.getScaledWindowHeight());
        float h = Math.min(context.getScaledWindowWidth() / f, context.getScaledWindowHeight() / f);
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(f * h);
        int k = (context.getScaledWindowWidth() - i) / 2;
        int l = (context.getScaledWindowHeight() - j) / 2;
        int m = k + i;
        int n = l + j;
        RenderSystem.enableBlend();
        context.drawTexture(SPYGLASS_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
        RenderSystem.disableBlend();
        context.fill(RenderLayer.getGuiOverlay(), 0, n, context.getScaledWindowWidth(), context.getScaledWindowHeight(), -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, context.getScaledWindowWidth(), l, -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), m, l, context.getScaledWindowWidth(), n, -90, Colors.BLACK);
    }
}
