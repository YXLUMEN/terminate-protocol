package lumen.terminate_protocol.network.packet;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record WeaponFireResultSyncS2CPacket(Vec3d start, Vec3d end, boolean important) implements CustomPayload {
    public static final Identifier WPN_FIRE_SYNC_ID = Identifier.of(TerminateProtocol.MOD_ID, "wpn_fire_sync");
    public static final Id<WeaponFireResultSyncS2CPacket> ID = new Id<>(WPN_FIRE_SYNC_ID);
    public static final PacketCodec<PacketByteBuf, WeaponFireResultSyncS2CPacket> CODEC = PacketCodec.of((value, buf) -> {
        buf.writeVec3d(value.start);
        buf.writeVec3d(value.end);
        buf.writeBoolean(value.important);
    }, buf -> new WeaponFireResultSyncS2CPacket(buf.readVec3d(), buf.readVec3d(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
