package lumen.terminate_protocol;

import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.entity_render.EmptyEntityRender;
import lumen.terminate_protocol.firearm_handler.GunActiveHandler;
import lumen.terminate_protocol.network.TPClientNetwork;
import lumen.terminate_protocol.render.FlashEffectRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TerminateProtocolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(FlashEffectRenderer::render);

        TPKeyBind.register();
        GunActiveHandler.register();
        TPClientNetwork.registryNetworks();

        EntityRendererRegistry.register(TPEntities.SMOKE_EFFECT_AREA, EmptyEntityRender::new);
    }
}