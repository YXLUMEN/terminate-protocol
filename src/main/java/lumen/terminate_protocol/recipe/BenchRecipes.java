package lumen.terminate_protocol.recipe;

import lumen.terminate_protocol.item.TPItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BenchRecipes extends FabricRecipeProvider {
    public BenchRecipes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.SMOKE_GRENADE, 2)
                .pattern("IRI")
                .pattern("IGI")
                .pattern("III")
                .input('I', Items.IRON_NUGGET)
                .input('G', Items.GUNPOWDER)
                .input('R', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(TPItems.SMOKE_GRENADE), FabricRecipeProvider.conditionsFromItem(TPItems.SMOKE_GRENADE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.FLASH_GRENADE, 1)
                .pattern("IRI")
                .pattern("IGI")
                .pattern("III")
                .input('I', Items.IRON_NUGGET)
                .input('G', Items.GLOWSTONE_DUST)
                .input('R', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(TPItems.FLASH_GRENADE), FabricRecipeProvider.conditionsFromItem(TPItems.FLASH_GRENADE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.INCENDIARY_GRENADE, 1)
                .pattern("IRI")
                .pattern("IGI")
                .pattern("III")
                .input('I', Items.IRON_NUGGET)
                .input('G', Items.BLAZE_POWDER)
                .input('R', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(TPItems.INCENDIARY_GRENADE), FabricRecipeProvider.conditionsFromItem(TPItems.INCENDIARY_GRENADE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.FRAG_GRENADE, 1)
                .pattern("IRI")
                .pattern("IGI")
                .pattern("III")
                .input('I', Items.IRON_NUGGET)
                .input('G', Items.IRON_INGOT)
                .input('R', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(TPItems.FRAG_GRENADE), FabricRecipeProvider.conditionsFromItem(TPItems.FRAG_GRENADE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.BATTERY, 1)
                .pattern("ILI")
                .pattern("IGI")
                .pattern("ILI")
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GOLDEN_APPLE)
                .input('L', Items.LAPIS_LAZULI)
                .criterion(FabricRecipeProvider.hasItem(TPItems.BATTERY), FabricRecipeProvider.conditionsFromItem(TPItems.BATTERY))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.CELL, 2)
                .pattern("ILI")
                .pattern("IGI")
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GOLDEN_APPLE)
                .input('L', Items.LAPIS_LAZULI)
                .criterion(FabricRecipeProvider.hasItem(TPItems.CELL), FabricRecipeProvider.conditionsFromItem(TPItems.CELL))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.MED_KIT, 1)
                .pattern("RRR")
                .pattern("RGR")
                .pattern("INI")
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GOLDEN_CARROT)
                .input('R', Items.RED_WOOL)
                .input('N', Items.IRON_NUGGET)
                .criterion(FabricRecipeProvider.hasItem(TPItems.MED_KIT), FabricRecipeProvider.conditionsFromItem(TPItems.MED_KIT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.PHOENIX_KIT, 1)
                .pattern("ILI")
                .pattern("GEG")
                .pattern("ILI")
                .input('I', Items.IRON_INGOT)
                .input('E', Items.ENCHANTED_GOLDEN_APPLE)
                .input('G', Items.GOLDEN_CARROT)
                .input('L', Items.LAPIS_LAZULI)
                .criterion(FabricRecipeProvider.hasItem(TPItems.PHOENIX_KIT), FabricRecipeProvider.conditionsFromItem(TPItems.PHOENIX_KIT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.LIGHT_AMMO, 16)
                .pattern("EGE")
                .pattern("GEG")
                .pattern("EGE")
                .input('E', Items.IRON_NUGGET)
                .input('G', Items.GUNPOWDER)
                .criterion(FabricRecipeProvider.hasItem(TPItems.LIGHT_AMMO), FabricRecipeProvider.conditionsFromItem(TPItems.LIGHT_AMMO))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.HEAVY_AMMO, 12)
                .pattern("EGE")
                .pattern("GIG")
                .pattern("EGE")
                .input('I', Items.IRON_INGOT)
                .input('E', Items.IRON_NUGGET)
                .input('G', Items.GUNPOWDER)
                .criterion(FabricRecipeProvider.hasItem(TPItems.HEAVY_AMMO), FabricRecipeProvider.conditionsFromItem(TPItems.HEAVY_AMMO))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TPItems.SNIPER_AMMO, 4)
                .pattern("IGI")
                .pattern("GEG")
                .pattern("IGI")
                .input('E', Items.GOLD_INGOT)
                .input('G', Items.GUNPOWDER)
                .input('I', Items.IRON_NUGGET)
                .criterion(FabricRecipeProvider.hasItem(TPItems.SNIPER_AMMO), FabricRecipeProvider.conditionsFromItem(TPItems.SNIPER_AMMO))
                .offerTo(recipeExporter);
    }
}
