package lumen.terminate_protocol.util.weapon;

public interface WeaponAccessor {
    boolean getWpnAiming();

    void setWpnAiming(boolean aiming);

    WeaponCooldownManager getWpnCooldownManager();
}
