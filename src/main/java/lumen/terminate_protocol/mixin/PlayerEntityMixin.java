package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.api.EntityShieldAccessor;
import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements EntityShieldAccessor, WeaponAccessor {
    @Unique
    private float maxShield = 0.0f;
    @Unique
    private float shieldAmount = 0.0f;
    @Unique
    private boolean isAimingWithWpn = false;

    // 枪械开火速率控制
    @Unique
    private final WeaponCooldownManager weaponCooldownManager = this.createWeaponCooldownManager();

    @Unique
    protected WeaponCooldownManager createWeaponCooldownManager() {
        return new WeaponCooldownManager();
    }

    // 护盾实现
    @Redirect(
            method = "applyDamage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
    private float modifyDamageAmount(PlayerEntity player, DamageSource source, float amount) {
        amount = this.modifyAppliedDamage(source, amount);

        if (this.shieldAmount <= 0) return amount;

        float remaining = Math.max(amount * 0.9f - this.shieldAmount, 0.0f);
        this.terminate_protocol$setShieldAmount(this.shieldAmount - (amount - remaining));

        if (this.maxShield <= 0 || this.shieldAmount > 0) return remaining;
        this.maxShield = 0.0f;

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                TPSoundEvents.SHIELD_CRASH, SoundCategory.NEUTRAL);

        return remaining;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeShield(@NotNull NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("TMaxShield", this.maxShield);
        nbt.putFloat("TShieldAmount", this.shieldAmount);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readShield(@NotNull NbtCompound nbt, CallbackInfo ci) {
        this.terminate_protocol$setMaxShieldAmount(nbt.getFloat("TMaxShield"));
        this.terminate_protocol$setShieldAmount(nbt.getFloat("TShieldAmount"));
    }

    public float terminate_protocol$getShieldAmount() {
        return this.shieldAmount;
    }

    public void terminate_protocol$setShieldAmount(float amount) {
        this.shieldAmount = MathHelper.clamp(amount, 0.0f, this.maxShield);
    }

    public float terminate_protocol$getMaxShieldAmount() {
        return this.maxShield;
    }

    public void terminate_protocol$setMaxShieldAmount(float amount) {
        this.maxShield = Math.max(amount, 0.0f);
    }

    // 枪械开火速率
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.weaponCooldownManager.update();
    }

    public WeaponCooldownManager terminate_protocol$getWpnCooldownManager() {
        return this.weaponCooldownManager;
    }

    public void terminate_protocol$setWpnAiming(boolean aiming) {
        this.isAimingWithWpn = aiming;
    }

    public boolean terminate_protocol$getWpnAiming() {
        return this.isAimingWithWpn;
    }
}
