package lumen.terminate_protocol.util.weapon;

import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.WeaponCooldownUpdateS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerWeaponCooldownManager extends WeaponCooldownManager {
    private final ServerPlayerEntity player;

    public ServerWeaponCooldownManager(ServerPlayerEntity player) {
        this.player = player;
    }

    protected void onCooldownUpdate(WeaponItem item, int duration) {
        super.onCooldownUpdate(item, duration);
        ServerPlayNetworking.send(this.player, new WeaponCooldownUpdateS2CPacket(item, duration));
    }

    protected void onCooldownUpdate(WeaponItem item) {
        super.onCooldownUpdate(item);
        ServerPlayNetworking.send(this.player, new WeaponCooldownUpdateS2CPacket(item, 0));
    }
}
