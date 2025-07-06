package lumen.terminate_protocol.api;


import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface TPDamageTypes {
    RegistryKey<DamageType> FLASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "flash"));
    RegistryKey<DamageType> FRAGMENT_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "fragment_hit"));
    RegistryKey<DamageType> LIGHT_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "light_bullet"));
    RegistryKey<DamageType> HEAVY_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "heavy_bullet"));
    RegistryKey<DamageType> ENERGY_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "energy_bullet"));
    RegistryKey<DamageType> SNIPER_BULLET_HIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TerminateProtocol.MOD_ID, "sniper_bullet"));
}
