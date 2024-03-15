package com.tac.guns.compat.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tac.guns.crafting.GunSmithTableResult;
import com.tac.guns.item.builder.AmmoItemBuilder;
import com.tac.guns.item.builder.GunItemBuilder;
import com.tac.guns.resource.CommonGunPackLoader;
import com.tac.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GunSmithTableResultWrapper {
    private ResourceLocation outputId;
    private int count = 1;
    private String craftingType;
    private String group;

    public GunSmithTableResultWrapper(ResourceLocation id, String type) {
        outputId = id;
        craftingType = type;
    }

    public GunSmithTableResultWrapper(JsonElement o) {
        if (o instanceof JsonObject) {
            craftingType = ((JsonObject) o).get("type").getAsString();
            String idName = ((JsonObject) o).get("id").getAsString();
            outputId = new ResourceLocation(idName);
            count = ((JsonObject) o).get("count").getAsInt();
        }
    }
    
    public ItemStack getResultItemStack() {
        if (craftingType.equals(GunSmithTableResult.GUN)) {
            return getGunItemStack(outputId, count);
        }
        if (craftingType.equals(GunSmithTableResult.AMMO)) {
            return getAmmoItemStack(outputId, count);
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getGunItemStack(ResourceLocation id, int count) {
        CommonGunIndex gunIndex = CommonGunPackLoader.getGunIndex(id).get();
        group = gunIndex.getType();
        ItemStack gunStack = GunItemBuilder.create().setCount(count).setId(id).setAmmoCount(0).setFireMode(gunIndex.getGunData().getFireModeSet().get(0)).build();
        return gunStack;
    }

    private ItemStack getAmmoItemStack(ResourceLocation id, int count) {
        group = "ammo";
        return AmmoItemBuilder.create().setCount(count).setId(id).build();
    }

    public GunSmithTableResultWrapper setCount(int i) {
        count = i;
        return this;
    }

    public GunSmithTableResult getResult() {
        ItemStack stack = getResultItemStack();
        return new GunSmithTableResult(stack, group);
    }

    public JsonObject getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", craftingType);
        object.addProperty("id", outputId.toString());
        object.addProperty("count", count);
        return object;
    }

    public ResourceLocation generateRecipeId() {
        String namespace = outputId.getNamespace();
        String path = craftingType + "/" + outputId.getPath();
        return new ResourceLocation(namespace, path);
    }
}
