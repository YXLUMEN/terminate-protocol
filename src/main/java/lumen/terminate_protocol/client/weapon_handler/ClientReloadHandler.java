package lumen.terminate_protocol.client.weapon_handler;

import lumen.terminate_protocol.TPComponentTypes;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.item.weapon.WeaponItem;
import lumen.terminate_protocol.network.packet.WeaponReloadC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import static lumen.terminate_protocol.util.weapon.WeaponHelper.findItemSlot;

public class ClientReloadHandler {
    public static void startReload(ClientPlayerEntity player, ItemStack itemStack) {
        if (itemStack.getDamage() == 0 || itemStack.getOrDefault(TPComponentTypes.WPN_RELOADING, false)) {
            return;
        }

        WeaponItem item = ((WeaponItem) itemStack.getItem());
        if (findItemSlot(player, item.getSettings().getAmmoType()) == -1 && findItemSlot(player, TPItems.DEBUG_AMMO) == -1) {
            return;
        }

        ClientPlayNetworking.send(new WeaponReloadC2SPacket());
    }
}
