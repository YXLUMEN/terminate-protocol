package lumen.terminate_protocol.util.track_algorithm;

import net.minecraft.util.math.Vec3d;

public class CubicBezierTracker implements TrackAlgorithm {
    private final float smoothness;
    private Vec3d p0, p1, p2;

    public CubicBezierTracker(float smoothness) {
        this.smoothness = smoothness;
    }

    @Override
    public void updatePosition(Vec3d currentPos, Vec3d targetPos, Vec3d currentVel) {
        this.p0 = currentPos;
        this.p2 = targetPos;

        Vec3d toTarget = targetPos.subtract(currentPos).normalize();
        this.p1 = p0.add(currentVel.multiply(smoothness)).add(toTarget.multiply(0.5 * p0.distanceTo(p2)));
    }

    @Override
    public Vec3d getAdjustedVelocity() {
        float t = Math.min(1, 0.1f / (float) p0.distanceTo(p2));

        Vec3d nextPos = p0.multiply((1 - t) * (1 - t))
                .add(p1.multiply(2 * (1 - t) * t))
                .add(p2.multiply(t * t));
        return nextPos.subtract(p0).normalize();
    }
}
