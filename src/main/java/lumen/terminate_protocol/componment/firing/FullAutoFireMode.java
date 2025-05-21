package lumen.terminate_protocol.componment.firing;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.getPlayerLookVec;

public class FullAutoFireMode implements IFireMod {
    @Override
    public boolean canFire(ServerPlayerEntity player, ItemStack stack) {
        if (stack.getDamage() >= stack.getMaxDamage()) return false;
        return !stack.getOrDefault(TPComponentTypes.WPN_PULLBOLT_TYPE, false);
    }

    @Override
    public void onFire(ServerPlayerEntity player, TrajectoryRayCaster rayCaster, WeaponItem item) {
        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = player.getEyePos();

        if (player.getWorld() instanceof ServerWorld world) {
            rayCaster.rayCast(world, player, muzzlePos, lookVec);
        }
    }
}
