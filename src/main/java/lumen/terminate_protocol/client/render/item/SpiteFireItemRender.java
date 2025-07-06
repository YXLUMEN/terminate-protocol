package lumen.terminate_protocol.client.render.item;

import lumen.terminate_protocol.client.model.item.SpiteFireItemModel;
import lumen.terminate_protocol.item.weapon.SpitfireItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SpiteFireItemRender extends GeoItemRenderer<SpitfireItem> {
    public SpiteFireItemRender() {
        super(new SpiteFireItemModel());
    }
}
