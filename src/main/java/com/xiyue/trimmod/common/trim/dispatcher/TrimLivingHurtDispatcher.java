package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.trim.context.PlayerTrimContext;
import com.xiyue.trimmod.common.trim.context.PlayerTrimContextFactory;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserSet;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSet;
import com.xiyue.trimmod.common.trim.set.ward.WardSet;
import com.xiyue.trimmod.common.util.MarkUtils;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class TrimLivingHurtDispatcher {
    private static final String SPIRE_PRIDE_KEY = "spire_pride_bonus";
    private static final ResourceKey<TrimPattern> RAISER_KEY =
            ResourceKey.create(Registries.TRIM_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "raiser"));

    private TrimLivingHurtDispatcher() {
    }

    public static void dispatch(LivingHurtEvent event) {
        Entity victimEntity = event.getEntity();
        Entity attackerEntity = event.getSource().getEntity();

        if (victimEntity instanceof Player player) {
            if (handleDefenderHurt(player, event)) {
                return;
            }
        }

        if (attackerEntity instanceof Player attacker) {
            handleAttackerHurt(attacker, event);
        }
    }

    private static boolean handleDefenderHurt(Player player, LivingHurtEvent event) {
        PlayerTrimContext context = PlayerTrimContextFactory.create(player);

        if (RaiserSet.COMBAT.handleFreezeImmunity(player, event, context.trimCount(RAISER_KEY) >= 4)) {
            return true;
        }

        if (event.getSource().is(DamageTypeTags.IS_FIRE) && context.trimCount(TrimPatterns.SNOUT) >= 4) {
            event.setCanceled(true);
            player.clearFire();
            return true;
        }

        if (player.getPersistentData().getInt("trimupgrade_snout_dash_ticks") > 0) {
            event.setAmount(event.getAmount() * 0.4f);
        }

        WardSet.COMBAT.onDefenderHurt(player, event, context.trimCount(TrimPatterns.WARD) == 4, context.minUpgradeLevel());

        if (RibSet.COMBAT.onFatalHurt(event, player)) {
            return true;
        }

        int wallTimer = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER);
        if (wallTimer > 0) {
            int level = context.minUpgradeLevel();
            float reduction = 0.28f + (level * 0.055f);
            event.setAmount(event.getAmount() * (1.0f - reduction));
        }

        float totalReduction = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) continue;
            int level = TrimUtils.getUpgradeLevel(stack);
            if (TrimUtils.isTrim(player, stack, TrimPatterns.WAYFINDER)) {
                totalReduction += (0.02f + 0.015f * level);
            }
            totalReduction += TrimCombatDispatcher.damageReductionForPiece(player, stack, level);
        }
        totalReduction = TrimCombatDispatcher.addFullSetDamageReduction(player, totalReduction);

        // Swamp passive: "腐藤缠身" - active below 40% max HP.
        SwampState swamp = resolveSwampState(player);
        if (swamp.active() && player.getHealth() < player.getMaxHealth() * 0.40F) {
            int level = Math.max(0, Math.min(4, swamp.minLevel()));
            totalReduction += 0.12f + 0.03f * level;
        }

        if (totalReduction > 0) {
            event.setAmount(event.getAmount() * (1.0f - Math.min(totalReduction, 0.95f)));
        }

        if (isContributorSet(player)) {
            float cap = player.getMaxHealth() * 0.33f;
            if (event.getAmount() > cap) {
                event.setAmount(cap);
            }
        }

        if (!player.level().isClientSide && event.getAmount() > 0.0F && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            SwampSet.TICK.interruptRegrowth(serverPlayer);
        }

        return false;
    }

    private static void handleAttackerHurt(Player attacker, LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (!attacker.level().isClientSide && attacker.level() instanceof ServerLevel serverLevel) {
            double bonusPct = MarkUtils.getWayfinderDamageBonusPct(target, serverLevel);
            if (bonusPct > 0.0D) {
                event.setAmount((float) (event.getAmount() * (1.0D + bonusPct)));
            }
        }

        EyeSet.COMBAT.onAttackerHurt(event, attacker);
        TrimCombatDispatcher.applyAttackerEffects(attacker, target);
        applySpirePrideFlatDamage(attacker, event);
    }

    private static void applySpirePrideFlatDamage(Player attacker, LivingHurtEvent event) {
        if (event.getAmount() <= 0.0F) {
            return;
        }
        if (event.getSource().getDirectEntity() != attacker) {
            return;
        }
        if (TrimUtils.getTrimCount(attacker, TrimPatterns.SPIRE) < 4) {
            return;
        }

        double prideBonus = attacker.getPersistentData().getDouble(SPIRE_PRIDE_KEY);
        if (prideBonus <= 0.0D) {
            return;
        }
        event.setAmount((float) (event.getAmount() + prideBonus));
    }

    private static boolean isContributorSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) return false;
        return TrimUtils.isTrim(player, head, TrimPatterns.WILD)
                && TrimUtils.isTrim(player, chest, TrimPatterns.WARD)
                && TrimUtils.isTrim(player, legs, TrimPatterns.COAST)
                && TrimUtils.isTrim(player, feet, TrimPatterns.TIDE);
    }

    private static SwampState resolveSwampState(Player player) {
        int tideCount = 0;
        int wildCount = 0;
        int minLevel = 99;

        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) continue;
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
        return new SwampState(active, active ? minLevel : 0);
    }

    private record SwampState(boolean active, int minLevel) {}
}


