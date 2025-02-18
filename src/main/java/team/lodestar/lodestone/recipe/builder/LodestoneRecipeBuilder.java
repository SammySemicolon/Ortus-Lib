package team.lodestar.lodestone.recipe.builder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.*;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.*;
import team.lodestar.lodestone.helpers.ReflectionHelper;

import java.util.Map;
import java.util.Optional;

public interface LodestoneRecipeBuilder<R extends Recipe<?>> {

    default void save(RecipeOutput recipeOutput, ItemLike definingItem) {
        save(recipeOutput, BuiltInRegistries.ITEM.getKey(definingItem.asItem()));
    }

    default void save(RecipeOutput recipeOutput, ResourceLocation id) {
        Advancement.Builder advancement$builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.tweakAdvancement(advancement$builder);
        this.write(recipeOutput, id, buildRecipe(id), advancement$builder);
    }

    default void tweakAdvancement(Advancement.Builder advancement) {
    }

    R buildRecipe(ResourceLocation id);

    default void write(RecipeOutput consumer, ResourceLocation id, R recipe, Advancement.Builder advancement) {
        String subfolder = getRecipeSubfolder();
        if (!subfolder.isEmpty()) {
            subfolder += "/";
        }
        consumer.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + subfolder)));
    }

    default String getRecipeSubfolder() {
        return "";
    }
}