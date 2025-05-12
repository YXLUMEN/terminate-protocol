package lumen.terminate_protocol.network;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record GunFireSyncS2CPayload(Vec3d start, Vec3d end) implements CustomPayload {
    public static final Identifier GUN_FIRE_SYNC_ID = Identifier.of(TerminateProtocol.MOD_ID, "gun_fire_sync");
    public static final Id<GunFireSyncS2CPayload> ID = new Id<>(GUN_FIRE_SYNC_ID);
    public static final PacketCodec<PacketByteBuf, GunFireSyncS2CPayload> CODEC = PacketCodec.of((value, buf) -> {
        buf.writeVec3d(value.start);
        buf.writeVec3d(value.end);
    }, buf -> new GunFireSyncS2CPayload(buf.readVec3d(), buf.readVec3d()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
