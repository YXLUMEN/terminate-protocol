package lumen.terminate_protocol.firearm_handler;

import lumen.terminate_protocol.item.guns.AbstractGunItem;
import lumen.terminate_protocol.network.GunFireC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static lumen.terminate_protocol.item.guns.AbstractGunItem.getMuzzleOffset;
import static lumen.terminate_protocol.util.RayCasterTools.getPlayerLookVec;

public class ClientFireHandler {
    private static long lastFireTick = 0;
    private static int lastFireCooldown = 0;

    public static void onFireTick(ClientPlayerEntity player, ItemStack itemStack, boolean isAiming) {
        if (itemStack.getDamage() >= itemStack.getMaxDamage() || player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return;

        long currentTick = player.getWorld().getTime();
        AbstractGunItem item = (AbstractGunItem) itemStack.getItem();

        if (currentTick - lastFireTick < lastFireCooldown) return;
        lastFireTick = currentTick;
        lastFireCooldown = item.getFireRant();

        Vec3d lookVec = getPlayerLookVec(player);
        Vec3d muzzlePos = isAiming ? player.getEyePos() : getMuzzleOffset(player, lookVec);

        // 开火特效
        createBulletTrack(player, muzzlePos);
        if (!isAiming) spawnMuzzleFlash(player, muzzlePos, lookVec);

        // 后坐力控制
        Random random = player.getRandom();
        float vertical = item.getVerticalRecoil(random);
        float horizontal = item.getHorizontalRecoil(random);
        float recoilFactor = player.isSneaking() ? 0.7f : 1.0f;

        GunRecoilSystem.applyRecoil(vertical * recoilFactor, horizontal * recoilFactor);

        GunFireC2SPayload payload = new GunFireC2SPayload(currentTick);
        ClientPlayNetworking.send(payload);
    }

    private static void createBulletTrack(ClientPlayerEntity player, Vec3d startPos) {
        World world = player.getWorld();

        Vec3d endPos = startPos.add(player.getRotationVec(1.0f).multiply(64));
        Vec3d velocity = endPos.subtract(startPos).multiply(0.8f);

        world.addParticle(ParticleTypes.END_ROD,
                startPos.x, startPos.y, startPos.z, velocity.x, velocity.y, velocity.z);
    }

    private static void spawnMuzzleFlash(ClientPlayerEntity player, Vec3d muzzlePos, Vec3d direction) {
        World world = player.getWorld();
        Random random = player.getRandom();

        world.addParticle(ParticleTypes.SMOKE,
                muzzlePos.x, muzzlePos.y, muzzlePos.z,
                direction.x * 0.1,
                0.02,
                direction.z * 0.1);


        if (random.nextFloat() < 0.5f) {
            world.addParticle(ParticleTypes.FLAME,
                    muzzlePos.x, muzzlePos.y, muzzlePos.z,
                    direction.x * 0.2 + random.nextGaussian() * 0.05,
                    0.05 + random.nextDouble() * 0.1,
                    direction.z * 0.2 + random.nextGaussian() * 0.05);
        }
    }
}
