package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.network.packet.WeaponAimC2SPacket;
import lumen.terminate_protocol.render.AimFovRender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientAimHandler {
    public static void setAim(boolean aiming, float fov) {
        AimFovRender.setTargetFOVMultiplier(fov);

        ClientPlayNetworking.send(new WeaponAimC2SPacket(aiming));
    }
}
