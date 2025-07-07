package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.api.EntityShieldAccessor;
import lumen.terminate_protocol.api.WeaponAccessor;
import lumen.terminate_protocol.sound.TPSoundEvents;
import lumen.terminate_protocol.util.weapon.WeaponCooldownManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
    private boolean isAimingWithWpn = false;

    @Unique
    private static final TrackedData<Float> SHIELD_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

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

        if (this.terminate_protocol$getShieldAmount() <= 0) return amount;

        float remaining = Math.max(amount * 0.9f - this.terminate_protocol$getShieldAmount(), 0.0f);
        this.terminate_protocol$setShieldAmount(this.terminate_protocol$getShieldAmount() - (amount - remaining));

        if (this.maxShield <= 0 || this.terminate_protocol$getShieldAmount() > 0) return remaining;
        this.maxShield = 0.0f;

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                TPSoundEvents.SHIELD_CRASH, SoundCategory.NEUTRAL);

        return remaining;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void injectDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(SHIELD_AMOUNT, 0.0f);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeShield(@NotNull NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("TMaxShield", this.maxShield);
        nbt.putFloat("TShieldAmount", this.terminate_protocol$getShieldAmount());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readShield(@NotNull NbtCompound nbt, CallbackInfo ci) {
        this.terminate_protocol$setMaxShieldAmount(nbt.getFloat("TMaxShield"));
        this.terminate_protocol$setShieldAmount(nbt.getFloat("TShieldAmount"));
    }

    // 枪械开火速率
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        this.weaponCooldownManager.update();
    }

    public WeaponCooldownManager terminate_protocol$getWpnCooldownManager() {
        return this.weaponCooldownManager;
    }

    public float terminate_protocol$getShieldAmount() {
        var player = (PlayerEntity) (Object) this;
        return player.getDataTracker().get(SHIELD_AMOUNT);
    }

    public void terminate_protocol$setShieldAmount(float amount) {
        var player = (PlayerEntity) (Object) this;
        player.getDataTracker().set(SHIELD_AMOUNT, MathHelper.clamp(amount, 0.0f, this.maxShield));
    }

    public float terminate_protocol$getMaxShieldAmount() {
        return this.maxShield;
    }

    public void terminate_protocol$setMaxShieldAmount(float amount) {
        this.maxShield = Math.max(amount, 0.0f);
    }

    public void terminate_protocol$setWpnAiming(boolean aiming) {
        this.isAimingWithWpn = aiming;
    }

    public boolean terminate_protocol$getWpnAiming() {
        return this.isAimingWithWpn;
    }
}
