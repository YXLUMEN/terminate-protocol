package lumen.terminate_protocol.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ICast {
    void start(World world, Entity attacker, Vec3d start, Vec3d dir);
}
