package com.xiyue.trimmod.common.trim.set.eye;

import com.xiyue.trimmod.common.trim.context.PlayerTrimContext;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public final class EyeCombat {
    public void onTargetChange(LivingChangeTargetEvent event, PlayerTrimContext context) {
        if (!(event.getEntity() instanceof EnderMan)) {
            return;
        }
        if (context.isFullSet(TrimPatterns.EYE)) {
            event.setCanceled(true);
        }
    }

    public void onCriticalHit(CriticalHitEvent event, Player player) {
        if (TrimUtils.getTrimCount(player, TrimPatterns.EYE) < 4) {
            return;
        }
        int level = TrimUtils.getMinUpgradeLevel(player);
        float critBonus = 0.06f + (level * 0.025f);
        event.setDamageModifier(event.getDamageModifier() + critBonus);
    }

    public void onAttackerHurt(LivingHurtEvent event, Player attacker) {
        if (TrimUtils.getTrimCount(attacker, TrimPatterns.EYE) < 4) {
            return;
        }
        int level = TrimUtils.getMinUpgradeLevel(attacker);
        float damageBonus = 0.06f + (level * 0.025f);
        event.setAmount(event.getAmount() * (1.0f + damageBonus));
    }

    public void onDefenderAttacked(LivingAttackEvent event, Player player) {
        if (player.level().isClientSide || TrimUtils.getTrimCount(player, TrimPatterns.EYE) < 4) {
            return;
        }
        int level = TrimUtils.getMinUpgradeLevel(player);
        float dodgeChance = 0.20f + (level * 0.05f);
        if (player.getRandom().nextFloat() >= dodgeChance) {
            return;
        }
        event.setCanceled(true);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
