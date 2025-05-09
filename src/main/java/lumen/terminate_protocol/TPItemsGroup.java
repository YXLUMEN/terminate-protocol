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
                            entries.add(VK47);
                            entries.add(MINI_GUN);

                            entries.add(FRAG_GRENADE);
                            entries.add(SMOKE_GRENADE);
                            entries.add(FLASH_GRENADE);
                            entries.add(INCENDIARY_GRENADE);
                            entries.add(BATTERY);
                            entries.add(CELL);
                            entries.add(MED_KIT);
                            entries.add(PHOENIX_KIT);
                        })
                        .build()
        );
    }
}
