package lumen.terminate_protocol.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class TPNetwork {
    public static void registerNetwork() {
        PayloadTypeRegistry.playS2C().register(FlashEffectS2CPayload.ID, FlashEffectS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BatterySoundInterruptS2CPayload.ID, BatterySoundInterruptS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GunFireSyncS2CPayload.ID, GunFireSyncS2CPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(GunFireC2SPayload.ID, GunFireC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GunReloadC2SPayload.ID, GunReloadC2SPayload.CODEC);

        ServerFireHandler.register();
    }
}
