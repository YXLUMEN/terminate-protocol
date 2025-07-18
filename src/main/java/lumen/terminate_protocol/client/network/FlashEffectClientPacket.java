package lumen.terminate_protocol.client.network;

import lumen.terminate_protocol.client.render.FlashEffectRenderer;
import lumen.terminate_protocol.network.packet.FlashEffectS2CPacket;
import lumen.terminate_protocol.sound.TPSoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;

public class FlashEffectClientPacket {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(FlashEffectS2CPacket.ID, (payload, context) -> {
            float strength = payload.strength();
            context.client().execute(() -> {
                FlashEffectRenderer.handleFlashEffect(strength);

                ClientWorld world = MinecraftClient.getInstance().world;
                ClientPlayerEntity player = MinecraftClient.getInstance().player;

                if (world == null || player == null || FlashEffectRenderer.getCurrentStrength() <= 1.2f) return;
                world.playSound(player, player.getX(), player.getY(), player.getZ(),
                        TPSoundEvents.TINNITUS, SoundCategory.PLAYERS, 0.1f, 0.8f);
            });
        });
    }
}
