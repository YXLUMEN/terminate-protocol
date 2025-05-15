package lumen.terminate_protocol.sound;

import lumen.terminate_protocol.TerminateProtocol;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class TPSoundEvents {
    public static final SoundEvent BULLET_RICOCHET = registerSound("bullet_ricochet");
    public static final SoundEvent GAS = registerSound("gas");
    public static final SoundEvent PULL_BIN = registerSound("pull_bin");
    public static final SoundEvent THROW_GRENADE = registerSound("throw_grenade");
    public static final SoundEvent GRENADE_BOUNCE = registerSound("grenade_bounce");
    public static final SoundEvent TINNITUS = registerSound("tinnitus");

    public static final SoundEvent PHOENIX_KIT_CHARGE = registerSound("battery.phoenix_kit_charge");
    public static final SoundEvent BATTERY_CHARGE = registerSound("battery.battery_charge");
    public static final SoundEvent CELL_CHARGE = registerSound("battery.cell_charge");
    public static final SoundEvent BATTERY_CHARGE_START = registerSound("battery.battery_charge_start");
    public static final SoundEvent BATTERY_CHARGE_FAIL = registerSound("battery.battery_charge_fail");
    public static final SoundEvent BATTERY_CHARGE_FINISH_ENERGY = registerSound("battery.battery_charge_finish_en");
    public static final SoundEvent BATTERY_CHARGE_FINISH_MEC = registerSound("battery.battery_charge_finish_mec");

    public static final SoundEvent KIT_START = registerSound("kit.kit_start");
    public static final SoundEvent KIT_USING = registerSound("kit.kit_using");
    public static final SoundEvent KIT_FINISH = registerSound("kit.kit_finish");
    public static final SoundEvent KIT_FAIL = registerSound("kit.kit_fail");

    public static final SoundEvent SHIELD_CRASH_1 = registerSound("shield_crash_1");
    public static final SoundEvent SHIELD_CRASH_2 = registerSound("shield_crash_2");
    public static final SoundEvent SHIELD_CRASH_3 = registerSound("shield_crash_3");
    public static final SoundEvent SHIELD_CRASH_4 = registerSound("shield_crash_4");

    public static final SoundEvent LIGHT_BULLET_HIT_1 = registerSound("light_bullet_hit_1");
    public static final SoundEvent LIGHT_BULLET_HIT_2 = registerSound("light_bullet_hit_2");
    public static final SoundEvent LIGHT_BULLET_HIT_3 = registerSound("light_bullet_hit_3");
    public static final SoundEvent LIGHT_BULLET_HIT_4 = registerSound("light_bullet_hit_4");

    public static final SoundEvent KRABER_FIRE = registerSound("kraber.fire");
    public static final SoundEvent KRABER_BOLTBACK_1 = registerSound("kraber.reload_boltback_1");
    public static final SoundEvent KRABER_BOLTBACK_2 = registerSound("kraber.reload_boltback_2");
    public static final SoundEvent KRABER_BOLTBACK_3 = registerSound("kraber.reload_boltback_3");
    public static final SoundEvent KRABER_BOLTFORWARD_1 = registerSound("kraber.reload_boltforward_1");
    public static final SoundEvent KRABER_BOLTFORWARD_2 = registerSound("kraber.reload_boltforward_2");
    public static final SoundEvent KRABER_BOLTFORWARD_3 = registerSound("kraber.reload_boltforward_3");
    public static final SoundEvent KRABER_MAGIN = registerSound("kraber.reload_magin");
    public static final SoundEvent KRABER_MAGOUT = registerSound("kraber.reload_magout");

    public static final SoundEvent R99_FIRE_1 = registerSound("r99.fire_1");
    public static final SoundEvent R99_FIRE_2 = registerSound("r99.fire_2");
    public static final SoundEvent R99_FIRE_LOW_AMMO_1 = registerSound("r99.fire_low_ammo_1");
    public static final SoundEvent R99_FIRE_LOW_AMMO_2 = registerSound("r99.fire_low_ammo_2");
    public static final SoundEvent R99_MAGIN = registerSound("r99.reload_magin");
    public static final SoundEvent R99_MAGOUT = registerSound("r99.reload_magout");
    public static final SoundEvent R99_BOLTBACK = registerSound("r99.reload_boltback");
    public static final SoundEvent R99_BOLTFORWARD = registerSound("r99.reload_boltforward");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(TerminateProtocol.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
    }
}
