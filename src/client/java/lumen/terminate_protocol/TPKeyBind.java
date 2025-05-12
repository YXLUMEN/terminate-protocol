package lumen.terminate_protocol;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TPKeyBind {
    private static final String CATEGORY = "category.terminate-protocol.weapons";
    public static final KeyBinding RELOAD_GUN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.terminate-protocol.reload",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY)
    );

    public static void register() {
    }
}
