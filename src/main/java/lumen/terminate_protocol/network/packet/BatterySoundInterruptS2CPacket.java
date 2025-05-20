package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BatterySoundInterruptS2CPacket(int type) implements CustomPayload {
    public static final Identifier BATTERY_INTERRUPT_ID = Identifier.of(TerminateProtocol.MOD_ID, "battery_interruption");
    public static final Id<BatterySoundInterruptS2CPacket> ID = new Id<>(BATTERY_INTERRUPT_ID);
    public static final PacketCodec<PacketByteBuf, BatterySoundInterruptS2CPacket> CODEC = PacketCodec.of((value, buf) ->
            buf.writeInt(value.type), buf -> new BatterySoundInterruptS2CPacket(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
