package lumen.terminate_protocol.weapon_handler;

import lumen.terminate_protocol.item.guns.AbstractWeaponItem;
import lumen.terminate_protocol.network.GunReloadC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import static lumen.terminate_protocol.item.guns.AbstractWeaponItem.findItemSlot;

public class ClientReloadHandler {
    public static void startReload(ClientPlayerEntity player, ItemStack itemStack) {
        if (itemStack.getDamage() == 0 || player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return;
        if (findItemSlot(player, ((AbstractWeaponItem) itemStack.getItem()).getAmmo()) == -1 && !player.isCreative())
            return;

        ClientPlayNetworking.send(new GunReloadC2SPayload());
    }
}
