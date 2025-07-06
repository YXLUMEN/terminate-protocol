package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WeaponStartFireC2SPacket(boolean start) implements CustomPayload {
    public static final Identifier WPN_BOUNDARY_ID = Identifier.of(TerminateProtocol.MOD_ID, "wpn_boundary");
    public static final Id<WeaponStartFireC2SPacket> ID = new Id<>(WPN_BOUNDARY_ID);
    public static final PacketCodec<PacketByteBuf, WeaponStartFireC2SPacket> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.writeBoolean(value.start), buf -> new WeaponStartFireC2SPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
