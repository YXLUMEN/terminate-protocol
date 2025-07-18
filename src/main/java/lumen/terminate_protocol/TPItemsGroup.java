package lumen.terminate_protocol;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static lumen.terminate_protocol.item.TPItems.*;

public class TPItemsGroup {
    public static void registerItemsGroup() {
        Registry.register(
                Registries.ITEM_GROUP,
                Identifier.of(TerminateProtocol.MOD_ID, "terminate_protocol"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(SMOKE_GRENADE))
                        .displayName(Text.translatable("itemGroup.terminate-protocol"))
                        .entries((displayContext, entries) -> {
                            entries.add(R99);
                            entries.add(SPITFIRE);
                            entries.add(LSTAR);
                            entries.add(KRABER);
                            entries.add(ROCKET_LAUNCHER);

                            entries.add(FRAG_GRENADE);
                            entries.add(SMOKE_GRENADE);
                            entries.add(FLASH_GRENADE);
                            entries.add(INCENDIARY_GRENADE);
                            entries.add(BATTERY);
                            entries.add(CELL);
                            entries.add(MED_KIT);
                            entries.add(PHOENIX_KIT);

                            entries.add(LIGHT_AMMO);
                            entries.add(HEAVY_AMMO);
                            entries.add(ENERGY_AMMO);
                            entries.add(SHRAPNEL_AMMO);
                            entries.add(SNIPER_AMMO);
                            entries.add(ROCKET_AMMO);
                        })
                        .build()
        );
    }
}
