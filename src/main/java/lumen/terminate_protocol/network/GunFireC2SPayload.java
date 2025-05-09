package lumen.terminate_protocol.network;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GunFireC2SPayload(long fireTime) implements CustomPayload {
    public static final Identifier FIREARM_FIRE_ID = Identifier.of(TerminateProtocol.MOD_ID, "gun_fire");
    public static final Id<GunFireC2SPayload> ID = new Id<>(FIREARM_FIRE_ID);
    public static final PacketCodec<PacketByteBuf, GunFireC2SPayload> CODEC = PacketCodec.ofStatic((buf, value) ->
                    buf.writeLong(value.fireTime), buf -> new GunFireC2SPayload(buf.readLong()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
