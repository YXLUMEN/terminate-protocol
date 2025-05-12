package lumen.terminate_protocol.firearm_handler;

import lumen.terminate_protocol.network.GunReloadC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientReloadHandler {
    public static void startReload(ClientPlayerEntity player, ItemStack itemStack) {
        if (itemStack.getDamage() == 0 || player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return;
        player.swingHand(Hand.MAIN_HAND);
        ClientPlayNetworking.send(new GunReloadC2SPayload());
    }
}
