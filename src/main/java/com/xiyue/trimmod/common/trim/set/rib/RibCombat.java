package com.xiyue.trimmod.common.trim.set.rib;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class RibCombat {
    private static final String IMMORTAL_CD_KEY = "trimupgrade_rib_immortal_cd_until";
    private static final long IMMORTAL_CD_TICKS = 300L * 20L;

    public boolean onFatalHurt(LivingHurtEvent event, Player player) {
        if (TrimUtils.getTrimCount(player, TrimPatterns.RIB) < 4) {
            return false;
        }
        if (event.getAmount() < player.getHealth()) {
            return false;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            return false;
        }

        long now = player.level().getGameTime();
        long cdUntil = chest.getOrCreateTag().getLong(IMMORTAL_CD_KEY);
        if (cdUntil > now) {
            return false;
        }

        chest.getOrCreateTag().putLong(IMMORTAL_CD_KEY, now + IMMORTAL_CD_TICKS);
        event.setCanceled(true);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3 * 20, 3, true, true, true));
        player.heal(player.getMaxHealth() * 0.10F);

        if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.BONE_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 0.9F);
            serverLevel.sendParticles(ParticleTypes.WHITE_ASH,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    12, 0.4, 0.4, 0.4, 0.02);
        }
        return true;
    }
}
