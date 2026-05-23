package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.sentry.SentrySet;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.wild.WildSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class TrimCombatDispatcher {
    private TrimCombatDispatcher() {
    }

    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        VexSet.COMBAT.onCriticalHit(event, player);
        TideSet.COMBAT.onCriticalHit(event, player);
        EyeSet.COMBAT.onCriticalHit(event, player);
    }

    public static float damageReductionForPiece(Player player, ItemStack stack, int level) {
        return TideSet.COMBAT.damageReductionForPiece(player, stack, level);
    }

    public static float addFullSetDamageReduction(Player player, float totalReduction) {
        return totalReduction + VexSet.COMBAT.fullSetDamageReduction(player);
    }

    public static void applyAttackerEffects(Player attacker, LivingEntity target) {
        DuneSet.COMBAT.applyAttackerEffects(attacker, target);
        WildSet.COMBAT.onAttackerHurt(attacker, target);
    }

    public static void applySentryRemoteDamage(LivingHurtEvent event) {
        SentrySet.COMBAT.onRemoteDamage(event);
    }
}
