package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WeaponAimC2SPacket(boolean aiming) implements CustomPayload {
    public static final Identifier WPN_AIM_ID = Identifier.of(TerminateProtocol.MOD_ID, "wpn_aim");
    public static final Id<WeaponAimC2SPacket> ID = new Id<>(WPN_AIM_ID);
    public static final PacketCodec<PacketByteBuf, WeaponAimC2SPacket> CODEC = PacketCodec.ofStatic((buf, value) ->
            buf.writeBoolean(value.aiming), buf -> new WeaponAimC2SPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
