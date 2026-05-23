package com.xiyue.trimmod.common.trim.set.coast;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class CoastAttributes {
    private static final UUID TOUGHNESS_UUID = UUID.fromString("d1a2b3c4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");

    public void collect(int level, Totals totals) {
        totals.toughnessBonus += 0.2D + (0.1375D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID,
                "Coast Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double toughnessBonus;

        public double toughnessBonus() { return toughnessBonus; }
    }
}

