package lumen.terminate_protocol.damage_type;


import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface TPDamageTypes {
    RegistryKey<DamageType> FRAGMENT_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "fragment_hit"));
    RegistryKey<DamageType> LIGHT_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "light_bullet"));
    RegistryKey<DamageType> HEAVY_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "heavy_bullet"));
}
