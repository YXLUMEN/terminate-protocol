package lumen.terminate_protocol.item;


import lumen.terminate_protocol.api.EntityShieldAccessor;
import lumen.terminate_protocol.network.packet.BatterySoundInterruptS2CPacket;
import lumen.terminate_protocol.sound.TPSoundEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Battery extends Item {
    public static final int MAX_SHIELD = 20;
    private static final int BASE_COOLDOWN_TICKS = 15;

    private final int shield;
    private final int chargeTime;

    public Battery(Settings settings, int shield, int chargeTime) {
        super(settings);
        this.shield = shield;
        this.chargeTime = chargeTime;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(itemStack);
        }

        if (((EntityShieldAccessor) user).terminate_protocol$getShieldAmount() >= MAX_SHIELD) {
            return TypedActionResult.fail(itemStack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) {
            spawnChargingParticles(world, user, ParticleTypes.EFFECT, 1);
            return;
        }

        if (remainingUseTicks == chargeTime - 4) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_START, SoundCategory.PLAYERS);
            return;
        }

        if (remainingUseTicks == chargeTime - 8) {
            SoundEvent sound = this.shield >= 20 ? TPSoundEvents.BATTERY_CHARGE : TPSoundEvents.CELL_CHARGE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    sound, SoundCategory.PLAYERS);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_FINISH_ENERGY, SoundCategory.PLAYERS);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_FINISH_MEC, SoundCategory.PLAYERS);

            var shield = ((EntityShieldAccessor) user);
            float shieldCount = Math.min(MAX_SHIELD, shield.terminate_protocol$getShieldAmount() + this.shield);
            shield.terminate_protocol$setMaxShieldAmount(shieldCount);
            shield.terminate_protocol$setShieldAmount(shieldCount);

            if (user instanceof PlayerEntity player) player.getItemCooldownManager().set(this, BASE_COOLDOWN_TICKS);
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (remainingUseTicks <= 0) return;

        if (user instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, new BatterySoundInterruptS2CPacket(this.shield >= 20 ? 0 : 1));
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                TPSoundEvents.BATTERY_CHARGE_FAIL, SoundCategory.PLAYERS);
    }

    public static void spawnChargingParticles(World world, LivingEntity user, ParticleEffect particleType, float particleCount) {
        for (int i = 0; i < particleCount; i++) {
            world.addParticle(particleType,
                    user.getX() + (world.random.nextDouble() - 0.5),
                    user.getY() + 0.5 + world.random.nextDouble(),
                    user.getZ() + (world.random.nextDouble() - 0.5),
                    (world.random.nextDouble() - 0.5) * 0.1,
                    0.05,
                    (world.random.nextDouble() - 0.5) * 0.1);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return chargeTime;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
