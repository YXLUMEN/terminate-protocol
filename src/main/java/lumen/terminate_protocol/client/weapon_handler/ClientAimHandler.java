package lumen.terminate_protocol.client.weapon_handler;

import lumen.terminate_protocol.client.render.AimFovRender;
import lumen.terminate_protocol.network.packet.WeaponAimC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientAimHandler {
    public static void setAim(boolean aiming, float fov) {
        AimFovRender.setTargetFOVMultiplier(fov);

        ClientPlayNetworking.send(new WeaponAimC2SPacket(aiming));
    }
}
