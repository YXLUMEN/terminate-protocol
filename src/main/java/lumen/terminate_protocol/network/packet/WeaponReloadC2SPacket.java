package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WeaponReloadC2SPacket() implements CustomPayload {
    public static final Identifier WPN_RELOAD_ID = Identifier.of(TerminateProtocol.MOD_ID, "wpn_reload");
    public static final Id<WeaponReloadC2SPacket> ID = new Id<>(WPN_RELOAD_ID);
    public static final PacketCodec<PacketByteBuf, WeaponReloadC2SPacket> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.nioBuffer(), buf -> new WeaponReloadC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
