package lumen.terminate_protocol.network;

import lumen.terminate_protocol.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class TPNetwork {
    public static void registerNetwork() {
        PayloadTypeRegistry.playS2C().register(FlashEffectS2CPacket.ID, FlashEffectS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BatterySoundInterruptS2CPacket.ID, BatterySoundInterruptS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(WeaponFireResultSyncS2CPacket.ID, WeaponFireResultSyncS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(WeaponCooldownUpdateS2CPacket.ID, WeaponCooldownUpdateS2CPacket.CODEC);

        PayloadTypeRegistry.playC2S().register(WeaponFireC2SPacket.ID, WeaponFireC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WeaponReloadC2SPacket.ID, WeaponReloadC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(WeaponAimC2SPacket.ID, WeaponAimC2SPacket.CODEC);
    }
}
