package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.sound.TPSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void onGetHurtSound(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity.getMaxAbsorption() <= 0 || livingEntity.getAbsorptionAmount() > 0) return;

        EntityAttributeInstance absorption = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_ABSORPTION);
        if (absorption == null) return;

        absorption.setBaseValue(0);

        if (livingEntity.getWorld() instanceof ServerWorld world) {
            world.playSound(null,
                    livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                    TPSoundEvents.SHIELD_CRASH, SoundCategory.NEUTRAL);
            ci.cancel();
        }
    }
}
