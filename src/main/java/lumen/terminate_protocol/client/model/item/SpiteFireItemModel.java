package lumen.terminate_protocol.client.model.item;

import lumen.terminate_protocol.TerminateProtocol;
import lumen.terminate_protocol.item.weapon.SpitfireItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SpiteFireItemModel extends GeoModel<SpitfireItem> {
    private final Identifier model = Identifier.of(TerminateProtocol.MOD_ID, "geo/spitfire.geo.json");
    private final Identifier texture = Identifier.of(TerminateProtocol.MOD_ID, "textures/item/spitfire.png");
    private final Identifier animations = Identifier.of(TerminateProtocol.MOD_ID, "animations/spitfire.animation.json");

    @Override
    public Identifier getModelResource(SpitfireItem spitfireItem) {
        return model;
    }

    @Override
    public Identifier getTextureResource(SpitfireItem spitfireItem) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(SpitfireItem spitfireItem) {
        return animations;
    }
}
