package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.util.weapon.ServerWeaponCooldownManager;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    protected WeaponCooldownManager createWeaponCooldownManager() {
        return new ServerWeaponCooldownManager((ServerPlayerEntity) (Object) this);
    }
}
