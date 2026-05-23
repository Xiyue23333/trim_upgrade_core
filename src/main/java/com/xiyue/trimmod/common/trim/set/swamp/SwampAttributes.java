package com.xiyue.trimmod.common.trim.set.swamp;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class SwampAttributes {
    private static final UUID HP_UUID = UUID.fromString("5d2e0f41-5b8a-4f2f-8a5b-4f3d7c9b2f11");
    private static final UUID SPEED_UUID = UUID.fromString("7d8f1b52-2a7e-4d31-9d2a-3abf2f8c4c22");
    private static final UUID ARMOR_UUID = UUID.fromString("9c3a6e11-1f4d-4e90-9f35-5b9d18b6f333");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("a4b5c6d7-e8f9-4a1b-9c2d-3e4f50617283");

    public void collectTide(int level, Totals totals) {
        totals.tideCount++;
        totals.hpBonus += 1.0D + 1.0D * level;
        totals.speedBonus += 0.01D + 0.01D * level;
    }

    public void collectWild(int level, Totals totals) {
        totals.wildCount++;
        totals.armorBonus += 0.75D + 0.25D * level;
        totals.toughnessBonus += 0.1D + 0.1D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.MAX_HEALTH, HP_UUID, "Swamp HP", totals.hpBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.MOVEMENT_SPEED, SPEED_UUID, "Swamp Speed", totals.speedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applier.apply(Attributes.ARMOR, ARMOR_UUID, "Swamp Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID, "Swamp Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private int tideCount;
        private int wildCount;
        private double hpBonus;
        private double speedBonus;
        private double armorBonus;
        private double toughnessBonus;

        public int tideCount() { return tideCount; }
        public int wildCount() { return wildCount; }
        public double hpBonus() { return hpBonus; }
        public double speedBonus() { return speedBonus; }
        public double armorBonus() { return armorBonus; }
        public double toughnessBonus() { return toughnessBonus; }
    }
}
