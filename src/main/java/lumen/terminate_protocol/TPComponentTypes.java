package lumen.terminate_protocol;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TPComponentTypes {
    public static final ComponentType<Boolean> WPN_FIRING = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TerminateProtocol.MOD_ID, "wpn_firing"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static final ComponentType<Boolean> WPN_RELOADING = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TerminateProtocol.MOD_ID, "wpn_reloading"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static final ComponentType<Integer> WPN_COOLDOWN = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TerminateProtocol.MOD_ID, "wpn_cooldown"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<Boolean> WPN_OVERHEAT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TerminateProtocol.MOD_ID, "wpn_overheat"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static void registerComponent() {
    }
}
