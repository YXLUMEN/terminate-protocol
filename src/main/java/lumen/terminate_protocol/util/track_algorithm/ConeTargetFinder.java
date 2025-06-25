package lumen.terminate_protocol.util.track_algorithm;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class ConeTargetFinder {
    private final int searchRange;
    private final float angleWidth;

    public ConeTargetFinder(int range, float degrees) {
        this.searchRange = range;
        this.angleWidth = (float) Math.toRadians(degrees);
    }

    public LivingEntity findTarget(World world, Vec3d origin, Vec3d direction, LivingEntity owner) {
        List<LivingEntity> candidates = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(origin, origin).expand(searchRange),
                e -> isValidTarget(e, owner, origin, direction)
        );

        return candidates.stream().min(Comparator.comparingDouble(e ->
                angleToDirection(origin, direction, e.getPos())
        )).orElse(null);
    }

    // 判断目标是否在锥形区域内
    private boolean isValidTarget(LivingEntity entity, LivingEntity owner, Vec3d origin, Vec3d direction) {
        if (entity == owner || !TargetPredicate.DEFAULT.test(owner, entity)) {
            return false;
        }

        Vec3d toTarget = entity.getPos().subtract(origin).normalize();
        double angle = Math.acos(direction.dotProduct(toTarget));
        return angle <= angleWidth / 2;
    }

    // 计算目标与锥形轴线的角度差（弧度）
    private double angleToDirection(Vec3d origin, Vec3d direction, Vec3d targetPos) {
        Vec3d toTarget = targetPos.subtract(origin).normalize();
        return Math.acos(direction.dotProduct(toTarget));
    }
}
