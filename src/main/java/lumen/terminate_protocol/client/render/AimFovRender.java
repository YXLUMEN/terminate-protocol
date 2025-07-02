package lumen.terminate_protocol.client.render;

public class AimFovRender {
    private static final float TRANSITION_SPEED = 0.3f;
    private static float targetFovMultiplier = 1.0f;
    private static float currentFovMultiplier = 1.0f;

    public static void updateFov() {
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
}
