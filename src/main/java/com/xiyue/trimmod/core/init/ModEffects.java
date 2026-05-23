package com.xiyue.trimmod.core.init;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.effect.PharaoCurseEffect;
import com.xiyue.trimmod.common.effect.SoulSacrificeEffect;
import com.xiyue.trimmod.common.effect.WildPoisonEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TrimMOD.MODID);

    // 荒野纹饰效果
    public static final RegistryObject<MobEffect> WILD_POISON =
            EFFECTS.register("wild_poison", WildPoisonEffect::new);

    // 恼鬼纹饰效果
    public static final RegistryObject<MobEffect> SOUL_SACRIFICE =
            EFFECTS.register("soul_sacrifice", SoulSacrificeEffect::new);

    // 沙丘纹饰效果
    public static final RegistryObject<MobEffect> PHARAO_CURSE =
            EFFECTS.register("pharao_curse", PharaoCurseEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}