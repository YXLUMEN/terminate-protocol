package lumen.terminate_protocol.render.entity;

import lumen.terminate_protocol.TerminateProtocol;
import lumen.terminate_protocol.entity.HomingMissileEntity;
import lumen.terminate_protocol.model.entity.HomingMissileEntityModel;
import lumen.terminate_protocol.model.entity.TPEntityModelLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class HomingMissileRenderer extends EntityRenderer<HomingMissileEntity> {
    private static final Identifier TEXTURE = Identifier.of(TerminateProtocol.MOD_ID, "homing_missile");
    private final HomingMissileEntityModel model;

    public HomingMissileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new HomingMissileEntityModel(ctx.getPart(TPEntityModelLayers.MISSILE));
    }

    @Override
    public Identifier getTexture(HomingMissileEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(HomingMissileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int light) {
        matrices.push();

        Vec3d vel = entity.getVelocity();
        float calculatedYaw = (float) Math.toDegrees(Math.atan2(vel.z, vel.x));
        float calculatedPitch = (float) Math.toDegrees(Math.atan2(vel.y, Math.sqrt(vel.x * vel.x + vel.z * vel.z))) + 90.0F;

        this.model.setAngles(entity, 0, 0, entity.age + tickDelta, calculatedYaw, calculatedPitch);
        this.model.render(matrices, vcp.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, 1, 1);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vcp, light);

        spawnExhaustParticles(entity, entity.getRandom());
    }

    private void spawnExhaustParticles(HomingMissileEntity missile, Random random) {
        Vec3d vel = missile.getVelocity().normalize().multiply(-0.2);
        missile.getWorld().addParticle(
                ParticleTypes.FLAME,
                missile.getX() - vel.x,
                missile.getY() - vel.y,
                missile.getZ() - vel.z,
                vel.x * 0.5 + random.nextFloat() * 0.01,
                vel.y * 0.5 + random.nextFloat() * 0.01,
                vel.z * 0.5 + random.nextFloat() * 0.01
        );
    }
}
