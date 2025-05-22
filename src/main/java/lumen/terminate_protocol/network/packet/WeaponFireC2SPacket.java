package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WeaponFireC2SPacket() implements CustomPayload {
    public static final Identifier WPN_FIRE_ID = Identifier.of(TerminateProtocol.MOD_ID, "wpn_fire");
    public static final Id<WeaponFireC2SPacket> ID = new Id<>(WPN_FIRE_ID);
    public static final PacketCodec<PacketByteBuf, WeaponFireC2SPacket> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.nioBuffer(), buf -> new WeaponFireC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
