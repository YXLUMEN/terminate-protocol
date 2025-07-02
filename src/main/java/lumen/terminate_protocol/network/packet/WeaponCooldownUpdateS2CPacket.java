package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record WeaponCooldownUpdateS2CPacket(Item item, int duration) implements CustomPayload {
    public static final Identifier WEAPON_COOLDOWN_ID = Identifier.of(TerminateProtocol.MOD_ID, "weapon_cooldown");
    public static final Id<WeaponCooldownUpdateS2CPacket> ID = new Id<>(WEAPON_COOLDOWN_ID);
    public static final PacketCodec<RegistryByteBuf, WeaponCooldownUpdateS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryValue(RegistryKeys.ITEM),
            WeaponCooldownUpdateS2CPacket::item,
            PacketCodecs.VAR_INT,
            WeaponCooldownUpdateS2CPacket::duration,
            WeaponCooldownUpdateS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
