package lumen.terminate_protocol.componment.firing;

import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.util.weapon.TrajectoryRayCaster;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IFireMod {
    boolean canFire(ServerPlayerEntity player, ItemStack stack);

    void onFire(ServerPlayerEntity player, TrajectoryRayCaster rayCaster, WeaponItem item);
}
