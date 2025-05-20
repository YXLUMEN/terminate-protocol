package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GunFireC2SPacket() implements CustomPayload {
    public static final Identifier GUN_FIRE_ID = Identifier.of(TerminateProtocol.MOD_ID, "gun_fire");
    public static final Id<GunFireC2SPacket> ID = new Id<>(GUN_FIRE_ID);
    public static final PacketCodec<PacketByteBuf, GunFireC2SPacket> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.nioBuffer(), buf -> new GunFireC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
