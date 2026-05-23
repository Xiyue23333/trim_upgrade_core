package com.xiyue.trimmod.common.trim.set.wild;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class WildAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a");

    public void collect(int level, Totals totals) {
        totals.armorBonus += 1.0D + 0.4375D * level;
        totals.toughnessBonus += 0.2D + 0.15D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_UUID, "Wild Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID, "Wild Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double armorBonus;
        private double toughnessBonus;

        public double armorBonus() { return armorBonus; }
        public double toughnessBonus() { return toughnessBonus; }
    }
}
