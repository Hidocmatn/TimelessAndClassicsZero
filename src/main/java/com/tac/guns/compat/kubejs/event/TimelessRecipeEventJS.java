package com.tac.guns.compat.kubejs.event;

import com.tac.guns.compat.kubejs.recipe.TimelessGunSmithRecipeJS;
import com.tac.guns.crafting.GunSmithTableRecipe;
import com.tac.guns.resource.CommonAssetManager;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.AfterLoadRecipeTypeJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TimelessRecipeEventJS extends EventJS {
    private Map<ResourceLocation, GunSmithTableRecipe> recipeMap;
    private List<RecipeJS> originalRecipes;
    private List<RecipeJS> recipesToAdd;
    private Set<RecipeJS> recipesToRemove;
    private List<RecipeJS> recipesToModify;

    public TimelessRecipeEventJS(Map<ResourceLocation, GunSmithTableRecipe> map) {
        recipeMap = map;
        recipesToAdd = new ArrayList<>();
        recipesToRemove = new HashSet<>();
        recipesToModify = new ArrayList<>();
    }

    private List<RecipeJS> getOriginalRecipes() {
        if (this.originalRecipes == null) {
            originalRecipes = new ArrayList<>();
            recipeMap.values().forEach(recipe -> {
                RecipeJS recipeJS = new TimelessGunSmithRecipeJS();
                recipeJS.id = recipe.getId();
                recipeJS.originalRecipe = recipe;
                recipeJS.type = new AfterLoadRecipeTypeJS(recipeJS.originalRecipe.getSerializer());
                originalRecipes.add(recipeJS);
            });
        }
        return originalRecipes;
    }

    public RecipeJS add(Object[] args) {
        ListJS args1 = ListJS.of(args);
        RecipeJS recipe = new TimelessGunSmithRecipeJS();
        recipe.create(args1);
        recipesToAdd.add(recipe);
        return recipe;
    }

    public void remove(RecipeFilter filter) {
        originalRecipes.stream().filter(filter).forEach(recipeJS -> {
            recipesToRemove.add(recipeJS);
        });
    }

    public void replaceInput(RecipeFilter filter, IngredientJS input0, IngredientJS input1, boolean exact) {
        originalRecipes.stream().filter(filter).forEach(recipeJS -> {
            recipeJS.replaceInput(input0, input1, exact);
            recipesToModify.add(recipeJS);
        });
    };

    public void replaceInput(IngredientJS input0, IngredientJS input1, boolean exact) {
        replaceInput(RecipeFilter.ALWAYS_TRUE, input0, input1, exact);
    }

    public static void post(CommonAssetManager manager) {
        TimelessRecipeEventJS event = new TimelessRecipeEventJS(manager.getAllRecipes());
        event.post(ScriptType.STARTUP, "recipes.tac");
        event.recipesToAdd.forEach(recipeJS -> {
            try {
                recipeJS.originalRecipe = recipeJS.createRecipe();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        if (!event.recipesToAdd.isEmpty()) {
            event.getOriginalRecipes().addAll(event.recipesToAdd);
        }
        if (event.originalRecipes != null) {
            if (!event.recipesToRemove.isEmpty()) {
                event.originalRecipes.removeAll(event.recipesToRemove);
                manager.clearRecipes();
                for (RecipeJS recipeJS : event.originalRecipes) {
                    manager.putRecipe(recipeJS.id, (GunSmithTableRecipe) recipeJS.originalRecipe);
                }
            }
            else {
                if (!event.recipesToModify.isEmpty()) {
                    for (RecipeJS recipeJS : event.recipesToModify) {
                        manager.putRecipe(recipeJS.id, (GunSmithTableRecipe) recipeJS.originalRecipe);
                    }
                }
            }
        }
    }
}
