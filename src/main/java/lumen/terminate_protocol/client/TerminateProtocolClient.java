package lumen.terminate_protocol.client;

import lumen.terminate_protocol.client.model.entity.HomingMissileEntityModel;
import lumen.terminate_protocol.client.model.entity.TPEntityModelLayers;
import lumen.terminate_protocol.client.network.TPClientNetwork;
import lumen.terminate_protocol.client.render.FlashEffectRenderer;
import lumen.terminate_protocol.client.render.entity.EmptyEntityRender;
import lumen.terminate_protocol.client.render.entity.HomingMissileRenderer;
import lumen.terminate_protocol.client.weapon_handler.ClientWeaponActionHandler;
import lumen.terminate_protocol.entity.TPEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static lumen.terminate_protocol.client.render.FlashEffectRenderer.FLASH_LAYER;

public class TerminateProtocolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(FLASH_LAYER, FlashEffectRenderer::render);

        TPKeyBind.register();
        ClientWeaponActionHandler.register();
        TPClientNetwork.registryNetworks();

        EntityModelLayerRegistry.registerModelLayer(TPEntityModelLayers.MISSILE, HomingMissileEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(TPEntities.SMOKE_EFFECT_AREA, EmptyEntityRender::new);
        EntityRendererRegistry.register(TPEntities.HOMING_MISSILE_ENTITY, HomingMissileRenderer::new);
    }
}