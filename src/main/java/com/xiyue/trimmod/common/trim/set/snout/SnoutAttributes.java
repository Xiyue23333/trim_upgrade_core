package com.xiyue.trimmod.common.trim.set.snout;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class SnoutAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b");
    private static final UUID KNOCKBACK_UUID = UUID.fromString("f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c");

    public void collect(int level, Totals totals) {
        totals.armor += 1.0D + 0.5625D * level;
        totals.knockbackResistance += 0.03D + 0.02D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, ARMOR_UUID,
                "Snout Armor", totals.armor, AttributeModifier.Operation.ADDITION);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_UUID,
                "Snout Knockback", totals.knockbackResistance, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double armor;
        private double knockbackResistance;

        public double armor() { return armor; }
        public double knockbackResistance() { return knockbackResistance; }
    }
}
