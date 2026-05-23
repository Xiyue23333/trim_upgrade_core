package com.xiyue.trimmod.common.trim.set.sentry;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class SentryAttributes {
    private static final UUID SPEED_UUID = UUID.fromString("5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0d");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("6b7c8d9e-0f1a-2b3c-4d5e-6f7a8b9c0d1e");

    public void collect(int level, Totals totals) {
        totals.count++;
        totals.minLevel = Math.min(totals.minLevel, level);
        totals.speedBonus += 0.03D + level * 0.02D;
        totals.toughnessBonus += 0.15D + level * 0.0875D;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.MOVEMENT_SPEED, SPEED_UUID, "Sentry Speed", totals.speedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID, "Sentry Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private int count;
        private int minLevel = 99;
        private double speedBonus;
        private double toughnessBonus;

        public int count() { return count; }
        public int minLevel() { return count > 0 ? minLevel : 0; }
        public double speedBonus() { return speedBonus; }
        public double toughnessBonus() { return toughnessBonus; }
    }
}
