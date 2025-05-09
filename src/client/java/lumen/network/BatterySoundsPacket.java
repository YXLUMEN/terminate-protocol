package lumen.network;

import lumen.terminate_protocol.network.BatterySoundInterruptS2CPayload;
import lumen.terminate_protocol.sound.TPSoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class BatterySoundsPacket {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(BatterySoundInterruptS2CPayload.ID, (payload, context) -> {
            SoundEvent soundEvent = switch (payload.type()) {
                case 1 -> TPSoundEvents.CELL_CHARGE;
                case 2 -> TPSoundEvents.KIT_USING;
                case 3 -> TPSoundEvents.PHOENIX_KIT_CHARGE;
                default -> TPSoundEvents.BATTERY_CHARGE;
            };

            MinecraftClient.getInstance().getSoundManager().stopSounds(
                    soundEvent.getId(),
                    SoundCategory.PLAYERS);
        });
    }
}
