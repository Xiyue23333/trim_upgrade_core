package com.xiyue.trimmod.common.trim.set.shaper;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class ShaperAttributes {
    private static final UUID TOUGHNESS_UUID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    private static final UUID HEALTH_UUID = UUID.fromString("2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e");

    public void collect(int level, Totals totals) {
        totals.toughnessBonus += 0.15D + (0.1875D * level);
        totals.healthBonus += 1.0D + (0.5D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID,
                "Shaper Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.MAX_HEALTH, HEALTH_UUID,
                "Shaper Health", totals.healthBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double toughnessBonus;
        private double healthBonus;

        public double toughnessBonus() { return toughnessBonus; }
        public double healthBonus() { return healthBonus; }
    }
}

