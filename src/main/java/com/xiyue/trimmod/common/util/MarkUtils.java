package com.xiyue.trimmod.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class MarkUtils {
    private MarkUtils() {}

    private static final String WF_BONUS_PCT_KEY = "trimupgrade_wf_bonus_pct";
    private static final String WF_BONUS_UNTIL_KEY = "trimupgrade_wf_bonus_until";

    private static final UUID SILENCE_HL_SLOW_UUID = UUID.fromString("5c10c4b0-9b6f-4d5b-9a4c-3b8a5b2b2b1a");
    private static final String SILENCE_HL_SLOW_UNTIL_KEY = "trimupgrade_silence_hl_slow_until";

    public static void applyWayfinderDamageMark(LivingEntity target, ServerLevel level, double bonusPct, int durationTicks) {
        if (durationTicks <= 0 || bonusPct <= 0) return;

        long now = level.getGameTime();
        long until = now + durationTicks;

        CompoundTag data = target.getPersistentData();
        double prevPct = data.getDouble(WF_BONUS_PCT_KEY);
        long prevUntil = data.getLong(WF_BONUS_UNTIL_KEY);

        data.putDouble(WF_BONUS_PCT_KEY, Math.max(prevPct, bonusPct));
        data.putLong(WF_BONUS_UNTIL_KEY, Math.max(prevUntil, until));
    }

    public static double getWayfinderDamageBonusPct(LivingEntity target, ServerLevel level) {
        CompoundTag data = target.getPersistentData();
        long until = data.getLong(WF_BONUS_UNTIL_KEY);
        if (until <= level.getGameTime()) return 0.0D;
        return Math.max(0.0D, data.getDouble(WF_BONUS_PCT_KEY));
    }

    public static void applySilenceHighlightSlow(LivingEntity target, ServerLevel level, double slowAmountMultiplyTotal, int durationTicks) {
        if (durationTicks <= 0) return;
        if (slowAmountMultiplyTotal >= 0.0D) return;

        AttributeInstance speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        slowAmountMultiplyTotal = Math.max(-0.85D, slowAmountMultiplyTotal);

        speed.removeModifier(SILENCE_HL_SLOW_UUID);
        speed.addTransientModifier(new AttributeModifier(
                SILENCE_HL_SLOW_UUID,
                "Silence Highlight Slow",
                slowAmountMultiplyTotal,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));

        long until = level.getGameTime() + durationTicks;
        CompoundTag data = target.getPersistentData();
        long prevUntil = data.getLong(SILENCE_HL_SLOW_UNTIL_KEY);
        data.putLong(SILENCE_HL_SLOW_UNTIL_KEY, Math.max(prevUntil, until));
    }

    public static void tickCleanup(LivingEntity entity, ServerLevel level) {
        CompoundTag data = entity.getPersistentData();
        long now = level.getGameTime();

        if (data.contains(WF_BONUS_UNTIL_KEY) && data.getLong(WF_BONUS_UNTIL_KEY) <= now) {
            data.remove(WF_BONUS_UNTIL_KEY);
            data.remove(WF_BONUS_PCT_KEY);
        }

        if (data.contains(SILENCE_HL_SLOW_UNTIL_KEY) && data.getLong(SILENCE_HL_SLOW_UNTIL_KEY) <= now) {
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.removeModifier(SILENCE_HL_SLOW_UUID);
            }
            data.remove(SILENCE_HL_SLOW_UNTIL_KEY);
        }
    }
}

