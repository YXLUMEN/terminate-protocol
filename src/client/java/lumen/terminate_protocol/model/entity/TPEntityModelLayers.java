package lumen.terminate_protocol.model.entity;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class TPEntityModelLayers {
    public static final EntityModelLayer MISSILE = create("missile", "main");

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.of(TerminateProtocol.MOD_ID, id), layer);
    }
}
