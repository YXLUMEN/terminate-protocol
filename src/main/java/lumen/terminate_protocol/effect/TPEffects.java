package lumen.terminate_protocol.effect;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class TPEffects {
    public static final RegistryEntry<StatusEffect> FLASHED =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(TerminateProtocol.MOD_ID, "flashed"),
                    new FlashEffect());

    public static final RegistryEntry<StatusEffect> SMOKE_CLOAK =
            Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(TerminateProtocol.MOD_ID, "smoke_cloak"),
                    new SmokeCloak());

    public static void registerEffects() {
    }
}
