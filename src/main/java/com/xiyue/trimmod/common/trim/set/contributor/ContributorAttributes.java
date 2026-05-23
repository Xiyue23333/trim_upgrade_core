package com.xiyue.trimmod.common.trim.set.contributor;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class ContributorAttributes {
    private static final UUID ARMOR_ADD_UUID = UUID.fromString("2b6d4f0d-0b39-46d9-ae13-3cfd2c0a35e6");
    private static final UUID ARMOR_MULT_UUID = UUID.fromString("6e3a3d78-7e9c-4d40-9db4-9d7fb5b65f51");

    public void collect(int level, Totals totals) {
        totals.armorMultiplier += 0.02D + (0.01D * level);
        totals.armorAddition += 0.5D + (0.375D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_ADD_UUID,
                "Contributor Armor Add", totals.armorAddition, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ARMOR, ARMOR_MULT_UUID,
                "Contributor Armor Mult", totals.armorMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public static final class Totals {
        private double armorAddition;
        private double armorMultiplier;

        public double armorAddition() { return armorAddition; }
        public double armorMultiplier() { return armorMultiplier; }
    }
}

