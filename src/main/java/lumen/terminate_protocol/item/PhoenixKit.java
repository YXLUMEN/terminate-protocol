package lumen.terminate_protocol.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import lumen.terminate_protocol.network.packet.BatterySoundInterruptS2CPacket;
import lumen.terminate_protocol.sound.TPSoundEvents;

import java.util.Iterator;

import static lumen.terminate_protocol.item.Battery.spawnChargingParticles;


public class PhoenixKit extends Item {
    private static final int MAX_SHIELD = 20;
    private static final int BASE_COOLDOWN_TICKS = 20;
    private static final int CHARGE_TIME = 200;

    public PhoenixKit(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (user.getAbsorptionAmount() >= MAX_SHIELD && user.getHealth() >= user.getMaxHealth()) {
            return TypedActionResult.fail(itemStack);
        }

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(itemStack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) {
            spawnChargingParticles(world, user, ParticleTypes.INSTANT_EFFECT, 1);
            return;
        }

        if (remainingUseTicks == CHARGE_TIME - 4) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_START, SoundCategory.PLAYERS);
            return;
        }

        if (remainingUseTicks == CHARGE_TIME - 8) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.PHOENIX_KIT_CHARGE, SoundCategory.PLAYERS);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_FINISH_ENERGY, SoundCategory.PLAYERS);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    TPSoundEvents.BATTERY_CHARGE_FINISH_MEC, SoundCategory.PLAYERS);


            EntityAttributeInstance absorption = user.getAttributeInstance(EntityAttributes.GENERIC_MAX_ABSORPTION);
            if (absorption != null) {
                absorption.setBaseValue(MAX_SHIELD);
                user.setAbsorptionAmount(MAX_SHIELD);
            } else {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,
                        -1, 5, false, false, false));
            }

            user.setHealth(user.getMaxHealth());
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,
                    600, 1, false, false, false));

            if (user instanceof PlayerEntity player) {
                player.getHungerManager().add(20, 2);
                player.getItemCooldownManager().set(this, BASE_COOLDOWN_TICKS);
                player.clearStatusEffects();

                Iterator<StatusEffectInstance> iterator = player.getActiveStatusEffects().values().iterator();

                while (iterator.hasNext()) {
                    var type = iterator.next().getEffectType();
                    if (type.value().getCategory() == StatusEffectCategory.BENEFICIAL) continue;
                    player.removeStatusEffect(type);
                    iterator.remove();
                }
            }
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (remainingUseTicks <= 0) return;

        if (user instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, new BatterySoundInterruptS2CPacket(3));
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                TPSoundEvents.BATTERY_CHARGE_FAIL, SoundCategory.PLAYERS);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return CHARGE_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
