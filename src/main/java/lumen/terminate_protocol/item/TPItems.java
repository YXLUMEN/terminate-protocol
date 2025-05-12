package lumen.terminate_protocol.item;

import lumen.terminate_protocol.TerminateProtocol;
import lumen.terminate_protocol.item.grenade.*;
import lumen.terminate_protocol.item.guns.KerableItem;
import lumen.terminate_protocol.item.guns.MiniGunItem;
import lumen.terminate_protocol.item.guns.VK47Item;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TPItems {
    public static final Item SMOKE_GRENADE = register("smoke_grenade", new SmokeGrenadeItem(new Item.Settings().maxCount(32)));
    public static final Item FLASH_GRENADE = register("flash_grenade", new FlashGrenadeItem(new Item.Settings().maxCount(32)));
    public static final Item INCENDIARY_GRENADE = register("incendiary_grenade", new IncendiaryGrenadeItem(new Item.Settings().maxCount(32)));
    public static final Item FRAG_GRENADE = register("frag_grenade", new FragGrenadeItem(new Item.Settings().maxCount(32)));

    public static final Item BATTERY = register("battery", new Battery(
            new Item.Settings().maxCount(4), 20, 80));

    public static final Item CELL = register("cell", new Battery(
            new Item.Settings().maxCount(8), 5, 35));

    public static final Item MED_KIT = register("med_kit", new MedKit(
            new Item.Settings().maxCount(4), 140));

    public static final Item PHOENIX_KIT = register("phoenix_kit", new PhoenixKit(
            new Item.Settings().maxCount(2)));

    public static final Item HOWITZER_152 = register("howitzer152", new Howitzer152Item(new Item.Settings().maxCount(32)));

    public static final Item VK47 = register("vk47", new VK47Item(new Item.Settings()));
    public static final Item MINI_GUN = register("mini_gun", new MiniGunItem(new Item.Settings()));
    public static final Item KERABLE = register("kerable", new KerableItem(new Item.Settings()));

    public static <T extends Item> T register(String id, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(TerminateProtocol.MOD_ID, id), item);
    }

    public static void registerItems() {
    }
}
