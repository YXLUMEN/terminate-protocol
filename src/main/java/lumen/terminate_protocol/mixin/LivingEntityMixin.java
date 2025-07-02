package lumen.terminate_protocol.mixin;

import lumen.terminate_protocol.effect.TPEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected abstract float modifyAppliedDamage(DamageSource source, float amount);

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float onDamage(float amount) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.hasStatusEffect(TPEffects.SERIOUS_INJURY)) {
            var dataTracker = livingEntity.getDataTracker();
            var key = ((LivingEntityAccessor) livingEntity).getHealthData();

            dataTracker.set(key, MathHelper.clamp(livingEntity.getHealth() - amount, 0.0F, livingEntity.getMaxHealth()));
            return 0.0f;
        }
        return amount;
    }
}
