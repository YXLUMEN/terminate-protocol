package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ShieldAmountS2CPacket(float amount) implements CustomPayload {
    public static final Identifier SHIELD_AMOUNT_ID = Identifier.of(TerminateProtocol.MOD_ID, "shield_amount");
    public static final Id<ShieldAmountS2CPacket> ID = new Id<>(SHIELD_AMOUNT_ID);
    public static final PacketCodec<PacketByteBuf, ShieldAmountS2CPacket> CODEC = PacketCodec.of((value, buf) ->
            buf.writeFloat(value.amount), buf -> new ShieldAmountS2CPacket(buf.readFloat()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
