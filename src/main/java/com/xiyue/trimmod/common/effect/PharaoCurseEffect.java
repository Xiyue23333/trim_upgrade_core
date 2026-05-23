package com.xiyue.trimmod.common.effect;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class PharaoCurseEffect extends MobEffect {
    public PharaoCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0xC2B280);
    }

    public float getDamageAmplifier(int amplifier) {
        return 0.12f + (0.04f * amplifier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPharaoCurseVictimHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getEntity() == null) return;

        var inst = event.getEntity().getEffect(ModEffects.PHARAO_CURSE.get());
        if (inst == null) return;

        if (!(inst.getEffect() instanceof PharaoCurseEffect curseEffect)) return;

        float extra = curseEffect.getDamageAmplifier(inst.getAmplifier());
        if (extra <= 0) return;
        event.setAmount(event.getAmount() * (1.0f + extra));
    }
}
