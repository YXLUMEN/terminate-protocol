package lumen.terminate_protocol.render.entity;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptyEntityRender<T extends Entity> extends EntityRenderer<T> {
    public static final Identifier TEXTURE = Identifier.of(TerminateProtocol.MOD_ID, "textures/item/null.png");

    public EmptyEntityRender(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return TEXTURE;
    }
}
