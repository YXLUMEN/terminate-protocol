package lumen.terminate_protocol;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TPComponentTypes {
    public static final ComponentType<Boolean> GUN_RELOADING_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TerminateProtocol.MOD_ID, "gun_reloading"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static void registerComponent() {
    }
}
