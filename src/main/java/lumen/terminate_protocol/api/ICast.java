package lumen.terminate_protocol.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface ICast {
    void start(ServerWorld world, Entity attacker, Vec3d start, Vec3d dir);
}
