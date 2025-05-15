package lumen.terminate_protocol.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class PreciseHitDetection {
    public static final Map<HitBoxType, Float> DAMAGE_MULTIPLIERS = Map.of(
            HitBoxType.HEAD, 2.0f,
            HitBoxType.CHEST, 1.0f,
            HitBoxType.ARMS, 0.8f,
            HitBoxType.LEGS, 0.7f,
            HitBoxType.FEET, 0.5f
    );

    public static HitBoxType getHitBox(Entity target, Vec3d hitPos) {
        double yOffset = hitPos.y - target.getY();
        double height = target.getHeight();

        if (yOffset > height * 0.8) {
            return HitBoxType.HEAD;
        } else if (yOffset > height * 0.5) {
            return HitBoxType.CHEST;
        } else if (yOffset > height * 0.2) {
            return HitBoxType.LEGS;
        } else {
            return HitBoxType.FEET;
        }
    }
}

