package com.tac.guns.compat.kubejs;

import com.tac.guns.GunMod;
import com.tac.guns.compat.kubejs.recipe.TimelessGunSmithRecipeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.resources.ResourceLocation;

public class TimelessKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register(new ResourceLocation(GunMod.MOD_ID, "gun_smith_table_crafting"), TimelessGunSmithRecipeJS::new);
    }
}
