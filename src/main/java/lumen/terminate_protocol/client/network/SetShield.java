package lumen.terminate_protocol.client.network;

import lumen.terminate_protocol.api.EntityShieldAccessor;
import lumen.terminate_protocol.network.packet.MaxShieldS2CPacket;
import lumen.terminate_protocol.network.packet.ShieldAmountS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class SetShield {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ShieldAmountS2CPacket.ID, (payload, context) -> {
            float amount = payload.amount();
            context.client().execute(() -> {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player instanceof EntityShieldAccessor shield) {
                    shield.terminate_protocol$setShieldAmount(amount);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(MaxShieldS2CPacket.ID, (payload, context) -> {
            float amount = payload.amount();
            context.client().execute(() -> {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player instanceof EntityShieldAccessor shield) {
                    shield.terminate_protocol$setMaxShieldAmount(amount);
                }
            });
        });
    }
}
