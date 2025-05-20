package lumen.terminate_protocol.item.weapon;

import lumen.terminate_protocol.api.WeaponFireMode;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

public class WeaponSettings {
    private final int fireRate;
    private final int reloadTick;
    private final Item ammoType;

    private WeaponFireMode recoilType;
    private float aimFOVMultiplier = 1.0f;
    private float recoilDepthMultiplier = 1.0f;
    private Vec3d aimOffset = new Vec3d(0, 0, 0);

    public WeaponSettings(int fireRate, int reloadTick, Item ammoType, WeaponFireMode type) {
        this.fireRate = fireRate;
        this.reloadTick = reloadTick;

        this.ammoType = ammoType;
        this.recoilType = type;
    }

    public WeaponSettings setRecoilType(WeaponFireMode type) {
        this.recoilType = type;
        return this;
    }

    public WeaponSettings setAimFovMultiplier(float aimFOVMultiplier) {
        this.aimFOVMultiplier = aimFOVMultiplier;
        return this;
    }

    public WeaponSettings setRecoilDecayMultiplier(float recoilDepthMultiplier) {
        this.recoilDepthMultiplier = recoilDepthMultiplier;
        return this;
    }

    public WeaponSettings setAimOffset(Vec3d offset) {
        this.aimOffset = offset;
        return this;
    }

    public int getFireRate() {
        return fireRate;
    }

    public int getReloadTick() {
        return reloadTick;
    }

    public Item getAmmoType() {
        return ammoType;
    }

    public WeaponFireMode getFireMode() {
        return recoilType;
    }

    public float getAimFOVMultiplier() {
        return aimFOVMultiplier;
    }

    public float getRecoilDepthMultiplier() {
        return recoilDepthMultiplier;
    }

    public Vec3d getAimOffset() {
        return aimOffset;
    }
}
