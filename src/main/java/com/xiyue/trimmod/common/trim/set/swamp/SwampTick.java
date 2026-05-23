package com.xiyue.trimmod.common.trim.set.swamp;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class SwampTick {
    private static final int PREP_DURATION_TICKS = 8 * 20;
    private static final int HEAL_WINDOW_TICKS = 4 * 20;
    private static final double LOW_HP_RING_RADIUS = 1.0D;
    private static final int LOW_HP_RING_POINTS = 24;

    public void tickRegrowth(ServerPlayer player) {
        State state = resolveState(player);
        if (!state.active) {
            clearState(player.getPersistentData());
            return;
        }

        spawnLowHpPassiveRing(player.serverLevel(), player);

        CompoundTag data = player.getPersistentData();
        int minLevel = Math.max(0, Math.min(4, state.minLevel));
        int healInterval = Math.max(1, (int) Math.round((1.5D - 0.25D * minLevel) * 20.0D));

        if (!data.getBoolean(PersistentDataKeys.SWAMP_REGEN_ACTIVE)) {
            int prep = data.getInt(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS) + 1;
            if (prep >= PREP_DURATION_TICKS) {
                data.putBoolean(PersistentDataKeys.SWAMP_REGEN_ACTIVE, true);
                data.putInt(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS, 0);
                data.putInt(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS, 0);
                data.putInt(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS, PREP_DURATION_TICKS);
            } else {
                data.putInt(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS, prep);
            }
            return;
        }

        int activeTicks = data.getInt(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS) + 1;
        int healTicks = data.getInt(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS) + 1;

        if (healTicks >= healInterval) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
            healTicks = 0;
        }

        spawnHealingParticles(player.serverLevel(), player);

        if (activeTicks >= HEAL_WINDOW_TICKS) {
            data.putBoolean(PersistentDataKeys.SWAMP_REGEN_ACTIVE, false);
            data.putInt(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS, 0);
            data.putInt(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS, 0);
            data.putInt(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS, 0);
            return;
        }

        data.putInt(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS, activeTicks);
        data.putInt(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS, healTicks);
    }

    public void interruptRegrowth(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(PersistentDataKeys.SWAMP_REGEN_ACTIVE)) {
            return;
        }
        data.putBoolean(PersistentDataKeys.SWAMP_REGEN_ACTIVE, false);
        data.putInt(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS, 0);
        data.putInt(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS, 0);
        data.putInt(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS, 0);
    }

    private void spawnHealingParticles(ServerLevel level, ServerPlayer player) {
        if (player.tickCount % 10 != 0) {
            return;
        }
        level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                player.getX(), player.getY() + 1.0D, player.getZ(),
                3, 0.45D, 0.55D, 0.45D, 0.01D
        );
    }

    private void spawnLowHpPassiveRing(ServerLevel level, ServerPlayer player) {
        if (player.getHealth() >= player.getMaxHealth() * 0.40F) {
            return;
        }
        if ((player.tickCount & 1) != 0) {
            return;
        }

        double y = player.getY() + 0.05D;
        for (int i = 0; i < LOW_HP_RING_POINTS; i++) {
            double angle = Math.PI * 2.0D * i / LOW_HP_RING_POINTS;
            double x = player.getX() + Math.cos(angle) * LOW_HP_RING_RADIUS;
            double z = player.getZ() + Math.sin(angle) * LOW_HP_RING_RADIUS;
            level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void clearState(CompoundTag data) {
        data.remove(PersistentDataKeys.SWAMP_REGEN_PREP_TICKS);
        data.remove(PersistentDataKeys.SWAMP_REGEN_ACTIVE_TICKS);
        data.remove(PersistentDataKeys.SWAMP_REGEN_HEAL_TICKS);
        data.remove(PersistentDataKeys.SWAMP_REGEN_ACTIVE);
    }

    private State resolveState(ServerPlayer player) {
        int tideCount = 0;
        int wildCount = 0;
        int minLevel = 99;

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) {
                continue;
            }
            int level = TrimUtils.getUpgradeLevel(stack);
            if (TrimUtils.isTrim(player, stack, TrimPatterns.TIDE)) {
                tideCount++;
                minLevel = Math.min(minLevel, level);
            } else if (TrimUtils.isTrim(player, stack, TrimPatterns.WILD)) {
                wildCount++;
                minLevel = Math.min(minLevel, level);
            }
        }

        boolean active = tideCount >= 2 && wildCount >= 2;
        return new State(active, active ? minLevel : 0);
    }

    private record State(boolean active, int minLevel) {
    }
}
