package team.lodestar.lodestone.recipe.builder;

import net.minecraft.data.recipes.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import team.lodestar.lodestone.recipe.*;

import java.util.*;

public class NBTCarryRecipeBuilder extends ShapedRecipeBuilder implements LodestoneRecipeBuilder<NBTCarryRecipe> {

    public final Ingredient copyFrom;

    public NBTCarryRecipeBuilder(RecipeCategory category, ItemStack result, Ingredient copyFrom) {
        super(category, result);
        this.copyFrom = copyFrom;
    }

    @Override
    public NBTCarryRecipe buildRecipe(ResourceLocation id) {
        var recipe = new ShapedRecipe(
                Objects.requireNonNullElse(group, ""),
                RecipeBuilder.determineBookCategory(category),
                ensureValid(id),
                resultStack,
                showNotification
        );

        return new NBTCarryRecipe(recipe, this.copyFrom);
    }
}
