package lumen.terminate_protocol.util.track_algorithm;

import net.minecraft.util.math.Vec3d;

public class LinearTracker implements TrackAlgorithm {
    private final float trackingStrength;
    private Vec3d trackedPosition;

    public LinearTracker(float trackingStrength) {
        this.trackingStrength = trackingStrength;
    }

    @Override
    public void updatePosition(Vec3d currentPos, Vec3d targetPos, Vec3d currentVel) {
        Vec3d direction = targetPos.subtract(currentPos).normalize();
        this.trackedPosition = currentVel.add(direction.multiply(trackingStrength));
    }

    @Override
    public Vec3d getAdjustedVelocity() {
        return this.trackedPosition;
    }
}
