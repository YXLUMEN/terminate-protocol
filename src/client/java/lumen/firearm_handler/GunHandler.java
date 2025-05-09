package lumen.firearm_handler;

import lumen.render.GunRecoilRender;
import lumen.terminate_protocol.item.AbstractGunItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class GunHandler {
    public static final GunRecoilRender RECOIL_SYSTEM = new GunRecoilRender();
    private static final String CATEGORY = "category.terminate-protocol.weapons";
    private static KeyBinding reloadKey;

    public static void register() {
        reloadKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.terminate-protocol.reload",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        CATEGORY)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) RECOIL_SYSTEM.update(client.player);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            ItemStack itemStack = client.player.getMainHandStack();
            if (!(itemStack.getItem() instanceof AbstractGunItem)) return;

            if (client.options.attackKey.isPressed()) {
                ClientFireHandler.onFireTick(player, itemStack);
            }

            if (reloadKey.isPressed()) {
                ClientReloadHandler.startReload(player, itemStack);
            }
        });
    }
}
