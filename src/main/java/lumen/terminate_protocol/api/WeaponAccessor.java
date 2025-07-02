package lumen.terminate_protocol.api;

import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;

public interface WeaponAccessor {
    boolean terminate_protocol$getWpnAiming();

    void terminate_protocol$setWpnAiming(boolean aiming);

    WeaponCooldownManager terminate_protocol$getWpnCooldownManager();
}
