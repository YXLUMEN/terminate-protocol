package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MaxShieldS2CPacket(float amount) implements CustomPayload {
    public static final Identifier MAX_SHIELD_ID = Identifier.of(TerminateProtocol.MOD_ID, "max_shield");
    public static final Id<MaxShieldS2CPacket> ID = new Id<>(MAX_SHIELD_ID);
    public static final PacketCodec<PacketByteBuf, MaxShieldS2CPacket> CODEC = PacketCodec.of((value, buf) ->
            buf.writeFloat(value.amount), buf -> new MaxShieldS2CPacket(buf.readFloat()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
