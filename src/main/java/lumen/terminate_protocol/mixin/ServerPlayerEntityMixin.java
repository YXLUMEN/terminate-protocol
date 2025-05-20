package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.util.weapon.WeaponCooldownAccessor;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements WeaponCooldownAccessor {
    @Unique
    private final WeaponCooldownManager weaponCooldownManager = this.createWeaponCooldownManager();

    @Unique
    protected WeaponCooldownManager createWeaponCooldownManager() {
        return new WeaponCooldownManager();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.weaponCooldownManager.update();
    }

    @Unique
    @Override
    public WeaponCooldownManager getWeaponCooldownManager() {
        return this.weaponCooldownManager;
    }
}
