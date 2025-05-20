package lumen.terminate_protocol.network;

import lumen.terminate_protocol.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class TPNetwork {
    public static void registerNetwork() {
        PayloadTypeRegistry.playS2C().register(FlashEffectS2CPacket.ID, FlashEffectS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BatterySoundInterruptS2CPacket.ID, BatterySoundInterruptS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(WeaponFireResultSyncS2CPacket.ID, WeaponFireResultSyncS2CPacket.CODEC);

        PayloadTypeRegistry.playC2S().register(GunFireC2SPacket.ID, GunFireC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(GunReloadC2SPacket.ID, GunReloadC2SPacket.CODEC);
    }
}
