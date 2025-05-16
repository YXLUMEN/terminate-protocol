package lumen.terminate_protocol;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TPKeyBind {
    private static final String CATEGORY = "category.terminate-protocol.weapons";

//    public static final KeyBinding WEAPON_AIM_KEY = new KeyBinding("key.weapon.aim", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, CATEGORY);

//    public static final KeyBinding WEAPON_FIRE_KEY = new KeyBinding("key.weapon.fire", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, CATEGORY);

    public static final KeyBinding WEAPON_RELOAD_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.weapon.reload", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));

    public static void register() {
    }
}
