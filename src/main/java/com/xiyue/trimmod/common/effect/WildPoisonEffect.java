package com.xiyue.trimmod.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WildPoisonEffect extends MobEffect {
    public WildPoisonEffect() {
        super(MobEffectCategory.HARMFUL, 0x4E9331);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        float damage = 1.0F + (amplifier * 0.5F);

        int originalHurtTime = entity.invulnerableTime;

        entity.hurt(entity.damageSources().magic(), damage);

        entity.invulnerableTime = originalHurtTime;
        entity.hurtTime = 0; // 移除受击
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 12 == 0;
    }
}