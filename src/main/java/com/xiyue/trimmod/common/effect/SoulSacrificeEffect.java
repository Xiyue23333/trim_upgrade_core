package com.xiyue.trimmod.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SoulSacrificeEffect extends MobEffect {
    public SoulSacrificeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x990000);
    }

    public float getCritDamageBonus(int amplifier) {
        return 0.5f + (0.125f * amplifier);
    }
}