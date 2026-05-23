package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.silence.SilenceSet;
import com.xiyue.trimmod.common.trim.set.spire.SpireSet;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.AABB;

public final class TrimPlayerTickDispatcher {
    private static final String CONTRIBUTOR_SMASH_PENDING_KEY = "trimupgrade_contributor_smash_pending";
    private static final String CONTRIBUTOR_SMASH_AIRBORNE_KEY = "trimupgrade_contributor_smash_airborne";
    private static final String CONTRIBUTOR_SMASH_LVL_KEY = "trimupgrade_contributor_smash_lvl";
    private static final String CONTRIBUTOR_SMASH_NOFALL_UNTIL_KEY = "trimupgrade_contributor_smash_nofall_until";
    private static final String CONTRIBUTOR_SMASH_STARTED_KEY = "trimupgrade_contributor_smash_started";

    private TrimPlayerTickDispatcher() {
    }

    public static void dispatch(ServerPlayer player) {
        tickContributorSmash(player);
        TrimTickDispatcher.emitSetAuras(player);
        TrimTickDispatcher.tickDashState(player);

        Holder<TrimPattern> fullSetPattern = TrimUtils.getFullSetPatternHolder(player);
        boolean hasFullSet = fullSetPattern != null;
        int fullSetMinLvl = hasFullSet ? TrimUtils.getMinUpgradeLevel(player) : 0;

        boolean hasSpireSet = hasFullSet && fullSetPattern.is(TrimPatterns.SPIRE);
        boolean hasSilenceSet = hasFullSet && fullSetPattern.is(TrimPatterns.SILENCE);
        boolean hasRibSet = hasFullSet && fullSetPattern.is(TrimPatterns.RIB);
        boolean hasEyeSet = hasFullSet && fullSetPattern.is(TrimPatterns.EYE);
        boolean hasTideSet = hasFullSet && fullSetPattern.is(TrimPatterns.TIDE);
        boolean hasShaperSet = hasFullSet && fullSetPattern.is(TrimPatterns.SHAPER);
        boolean hasWayfinderSet = hasFullSet && fullSetPattern.is(TrimPatterns.WAYFINDER);
        boolean hasVexSet = hasFullSet && fullSetPattern.is(TrimPatterns.VEX);
        boolean hasDuneSet = hasFullSet && fullSetPattern.is(TrimPatterns.DUNE);
        boolean hasSnoutSet = hasFullSet && fullSetPattern.is(TrimPatterns.SNOUT);
        boolean hasCoastSet = hasFullSet && fullSetPattern.is(TrimPatterns.COAST);
        boolean hasAnySpirePiece = TrimUtils.getTrimCount(player, TrimPatterns.SPIRE) > 0;

        SpireSet.TICK.tickMajestyAura(player, hasSpireSet, fullSetMinLvl);
        SpireSet.TICK.tickAttackBonus(player, hasAnySpirePiece);
        SpireSet.TICK.tickPride(player, hasSpireSet);
        SilenceSet.TICK.syncResetIfNeeded(player, hasSilenceSet);
        RibSet.TICK.applyAttackBonus(player, hasRibSet);
        EyeSet.TICK.applyAttackDamageBonus(player, hasEyeSet, fullSetMinLvl);
        EyeSet.TICK.tickCharge(player, hasEyeSet);

        TrimTickDispatcher.tickSetState(player, hasTideSet, hasVexSet, hasDuneSet,
                hasSnoutSet, hasShaperSet, hasWayfinderSet, hasCoastSet, fullSetMinLvl);

        SwampSet.TICK.tickRegrowth(player);
        SwampSet.SKILL.tickActiveDomain(player);
    }

    private static void tickContributorSmash(ServerPlayer player) {
        var data = player.getPersistentData();
        if (!data.getBoolean(CONTRIBUTOR_SMASH_PENDING_KEY)) return;

        long now = player.level().getGameTime();
        player.fallDistance = 0;

        long started = data.getLong(CONTRIBUTOR_SMASH_STARTED_KEY);
        if (started > 0 && now - started > 200L) {
            clearContributorSmashState(data);
            return;
        }

        if (!data.getBoolean(CONTRIBUTOR_SMASH_AIRBORNE_KEY)) {
            if (!player.onGround()) {
                data.putBoolean(CONTRIBUTOR_SMASH_AIRBORNE_KEY, true);
            } else if (started > 0 && now - started > 20L) {
                clearContributorSmashState(data);
            }
            return;
        }

        if (!player.onGround()) return;

        int minLvl = Math.max(0, Math.min(4, data.getInt(CONTRIBUTOR_SMASH_LVL_KEY)));
        ServerLevel level = player.serverLevel();

        float damage = 8.0f + (minLvl * 4.0f);
        double radius = 4.0D;
        int slowTicks = 5 * 20;

        AABB area = player.getBoundingBox().inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive() && e != player && !(e instanceof Player)).forEach(target -> {
            target.hurt(player.damageSources().playerAttack(player), damage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slowTicks, 10, true, true, true));
        });

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10 * 20, 3, true, true, true));

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.9F, 0.9F + level.random.nextFloat() * 0.2F);
        level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 0.1, player.getZ(), 1, 0, 0, 0, 0);
        for (int i = 0; i < 360; i += 20) {
            double rad = Math.toRadians(i);
            double x = player.getX() + Math.cos(rad) * radius;
            double z = player.getZ() + Math.sin(rad) * radius;
            level.sendParticles(ParticleTypes.CLOUD, x, player.getY() + 0.15, z, 1, 0, 0, 0, 0.02);
            if (level.random.nextFloat() < 0.5f) {
                level.sendParticles(ParticleTypes.CRIT, x, player.getY() + 0.3, z, 1, 0, 0, 0, 0.0);
            }
        }

        data.putLong(CONTRIBUTOR_SMASH_NOFALL_UNTIL_KEY, now + 10L);
        data.remove(CONTRIBUTOR_SMASH_PENDING_KEY);
        data.remove(CONTRIBUTOR_SMASH_AIRBORNE_KEY);
        data.remove(CONTRIBUTOR_SMASH_LVL_KEY);
        data.remove(CONTRIBUTOR_SMASH_STARTED_KEY);
    }

    private static void clearContributorSmashState(net.minecraft.nbt.CompoundTag data) {
        data.remove(CONTRIBUTOR_SMASH_PENDING_KEY);
        data.remove(CONTRIBUTOR_SMASH_AIRBORNE_KEY);
        data.remove(CONTRIBUTOR_SMASH_LVL_KEY);
        data.remove(CONTRIBUTOR_SMASH_STARTED_KEY);
        data.remove(CONTRIBUTOR_SMASH_NOFALL_UNTIL_KEY);
    }
}
