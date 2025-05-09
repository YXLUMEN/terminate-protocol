package lumen;

import lumen.entity_render.EmptyEntityRender;
import lumen.firearm_handler.GunHandler;
import lumen.network.BatterySoundsPacket;
import lumen.network.FlashEffectClientPacket;
import lumen.render.FlashEffectRenderer;
import lumen.terminate_protocol.entity.TPEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TerminateProtocolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(FlashEffectRenderer::render);

        GunHandler.register();

        FlashEffectClientPacket.register();
        BatterySoundsPacket.register();

        EntityRendererRegistry.register(TPEntities.SMOKE_EFFECT_AREA, EmptyEntityRender::new);
    }
}