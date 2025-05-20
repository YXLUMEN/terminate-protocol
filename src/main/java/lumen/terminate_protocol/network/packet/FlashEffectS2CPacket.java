package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record FlashEffectS2CPacket(float strength) implements CustomPayload {
    public static final Identifier FLASH_EFFECT_ID = Identifier.of(TerminateProtocol.MOD_ID, "flash_effect");
    public static final Id<FlashEffectS2CPacket> ID = new Id<>(FLASH_EFFECT_ID);
    public static final PacketCodec<PacketByteBuf, FlashEffectS2CPacket> CODEC = PacketCodec.of((value, buf) ->
            buf.writeFloat(value.strength), buf -> new FlashEffectS2CPacket(buf.readFloat()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
