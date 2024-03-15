package com.tac.guns.compat.kubejs.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tac.guns.crafting.GunSmithTableIngredient;
import com.tac.guns.crafting.GunSmithTableRecipe;
import com.tac.guns.crafting.GunSmithTableResult;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TimelessGunSmithRecipeJS extends RecipeJS {
    private static final String INPUT_NAME = "materials";
    private static final String OUTPUT_NAME = "result";
    private GunSmithTableResultWrapper resultWrapper;

    public TimelessGunSmithRecipeJS() {}

    @Override
    public GunSmithTableRecipe createRecipe() throws Throwable{
        this.serializeJson();
        GunSmithTableResult result = resultWrapper.getResult();
        List<GunSmithTableIngredient> ingredientList = Lists.newArrayList();
        for (IngredientJS ingredient: inputItems) {
            GunSmithTableIngredient gunSmithTableIngredient = new GunSmithTableIngredient(ingredient.createVanillaIngredient(), ingredient.getCount());
            ingredientList.add(gunSmithTableIngredient);
        }
        return new GunSmithTableRecipe(resultWrapper.generateRecipeId(), result, ingredientList);
    }

    @Override
    public void create(ListJS args) {
        resultWrapper = new GunSmithTableResultWrapper(UtilsJS.getMCID(args.get(0)), String.valueOf(args.get(1)));
        outputItems.add(ItemStackJS.of(resultWrapper.getResultItemStack()));
    }

    @Override
    public void deserialize() {
        ItemStack stack = new GunSmithTableResultWrapper(json.get(OUTPUT_NAME)).getResultItemStack();
        outputItems.add(ItemStackJS.of(stack));
        JsonArray inputArray = GsonHelper.getAsJsonArray(json, INPUT_NAME);
        for (JsonElement e : inputArray) {
            JsonObject o = e.getAsJsonObject();
            int count = o.has("count") ? o.get("count").getAsInt() : 0;
            inputItems.add(parseIngredientItem(o.get("item")).withCount(count));
        }
    }

    @Override
    public void serialize() {
        if (serializeOutputs) {
            json.add(OUTPUT_NAME, resultWrapper.getJson());
        }
        if (serializeInputs) {
            JsonArray array = new JsonArray();
            for (IngredientJS ingredient : inputItems) {
                JsonObject o = new JsonObject();
                o.add("item", ingredient.toJson());
                o.addProperty("count", ingredient.getCount());
                array.add(o);
            }
            json.add(INPUT_NAME, array);
        }
    }

    public TimelessGunSmithRecipeJS addInputItem(Object o) {
        inputItems.add(parseIngredientItem(o));
        return this;
    }
    public TimelessGunSmithRecipeJS addInputItems(Object... objects) {
        for (Object o : objects) {
            inputItems.add(parseIngredientItem(o));
        }
        return this;
    }

    public TimelessGunSmithRecipeJS setResultCount(int i) {
        resultWrapper.setCount(i);
        return this;
    }
}
