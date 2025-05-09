package lumen.terminate_protocol.network;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BatterySoundInterruptS2CPayload(int type) implements CustomPayload {
    public static final Identifier BATTERY_INTERRUPT_ID = Identifier.of(TerminateProtocol.MOD_ID, "battery_interruption");
    public static final Id<BatterySoundInterruptS2CPayload> ID = new Id<>(BATTERY_INTERRUPT_ID);
    public static final PacketCodec<PacketByteBuf, BatterySoundInterruptS2CPayload> CODEC = PacketCodec.of((value, buf) ->
            buf.writeInt(value.type), buf -> new BatterySoundInterruptS2CPayload(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
