package lumen.terminate_protocol.util.track_algorithm;

import net.minecraft.util.math.Vec3d;

public interface TrackAlgorithm {
    void updatePosition(Vec3d missilePos, Vec3d targetPos, Vec3d currentVel);

    Vec3d getAdjustedVelocity();
}
