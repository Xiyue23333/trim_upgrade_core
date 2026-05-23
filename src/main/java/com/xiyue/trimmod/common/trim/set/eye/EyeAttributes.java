package com.xiyue.trimmod.common.trim.set.eye;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class EyeAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("b2755618-2d9c-462e-9051-787477a94d8a");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("1cc0fbbb-09ba-4767-bf81-d7c43ece5872");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("091aeefd-6987-4fee-8d80-425ff15ca681");

    public void collect(int level, Totals totals) {
        totals.armorPenalty -= (1.25D - (0.1875D * level));
        totals.toughnessPenalty -= (0.85D - (0.15D * level));
        totals.attackSpeedBonus += (0.025D + (0.01D * level));
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, ARMOR_UUID,
                "Eye Armor", totals.armorPenalty, AttributeModifier.Operation.ADDITION);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID,
                "Eye Toughness", totals.toughnessPenalty, AttributeModifier.Operation.ADDITION);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID,
                "Eye Attack Speed", totals.attackSpeedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final class Totals {
        private double armorPenalty;
        private double toughnessPenalty;
        private double attackSpeedBonus;

        public double armorPenalty() { return armorPenalty; }
        public double toughnessPenalty() { return toughnessPenalty; }
        public double attackSpeedBonus() { return attackSpeedBonus; }
    }
}
