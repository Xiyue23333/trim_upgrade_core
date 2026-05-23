package com.xiyue.trimmod.common.trim.set.silence;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class SilenceAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("eb3e3965-1af7-4b06-a0c9-38621f388fb3");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("d34b0653-609d-427c-92bf-6f10a6c5711b");

    public void collect(int level, Totals totals) {
        totals.armorBonus += 1.5D + (0.5D * level);
        totals.toughnessBonus += 0.5D + (0.25D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_UUID,
                "Silence Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID,
                "Silence Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double armorBonus;
        private double toughnessBonus;

        public double armorBonus() { return armorBonus; }
        public double toughnessBonus() { return toughnessBonus; }
    }
}

