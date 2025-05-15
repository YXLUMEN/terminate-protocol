package lumen.terminate_protocol;

import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.network.TPClientNetwork;
import lumen.terminate_protocol.render.FlashEffectRenderer;
import lumen.terminate_protocol.render.entity.EmptyEntityRender;
import lumen.terminate_protocol.weapon_handler.ClientWeaponActionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static lumen.terminate_protocol.render.FlashEffectRenderer.FLASH_LAYER;

public class TerminateProtocolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(FLASH_LAYER, FlashEffectRenderer::render);

        TPKeyBind.register();
        ClientWeaponActionHandler.register();
        TPClientNetwork.registryNetworks();

        EntityRendererRegistry.register(TPEntities.SMOKE_EFFECT_AREA, EmptyEntityRender::new);
    }
}