package com.xiyue.trimmod.data;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.data.advancement.ModAdvancementProvider;
import com.xiyue.trimmod.data.recipe.ModRecipeProvider;
import com.xiyue.trimmod.data.tag.ModBlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        if (event.includeServer()) {
            generator.addProvider(true, new ModBlockTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
            generator.addProvider(true, new ModRecipeProvider(output));
            generator.addProvider(true, new AdvancementProvider(output, event.getLookupProvider(),
                    List.of(new ModAdvancementProvider())));
        }
    }
}
