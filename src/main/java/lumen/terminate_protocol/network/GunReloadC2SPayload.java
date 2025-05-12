package lumen.terminate_protocol.network;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GunReloadC2SPayload() implements CustomPayload {
    public static final Identifier GUN_RELOAD_ID = Identifier.of(TerminateProtocol.MOD_ID, "gun_reload");
    public static final Id<GunReloadC2SPayload> ID = new Id<>(GUN_RELOAD_ID);
    public static final PacketCodec<PacketByteBuf, GunReloadC2SPayload> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.nioBuffer(), buf -> new GunReloadC2SPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
