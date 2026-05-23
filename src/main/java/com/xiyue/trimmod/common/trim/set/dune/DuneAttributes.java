package com.xiyue.trimmod.common.trim.set.dune;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class DuneAttributes {
    private static final UUID SPEED_UUID = UUID.fromString("d1e2f3a4-b5c6-4d7e-8f9a-0b1c2d3e4f5a");
    private static final UUID ARMOR_UUID = UUID.fromString("e2f3a4b5-c6d7-4e8f-9a0b-1c2d3e4f5a6b");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("f3a4b5c6-d7e8-4f9a-0b1c-2d3e4f5a6b7c");

    public void collect(int level, Totals totals) {
        totals.count++;
        totals.minLevel = Math.min(totals.minLevel, level);
        totals.speedBonus += 0.04D + level * 0.03D;
        totals.armorReduction -= 0.5D + level * 0.1875D;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, SPEED_UUID,
                "Dune Speed", totals.speedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, ARMOR_UUID,
                "Dune Armor Reduction", totals.armorReduction, AttributeModifier.Operation.ADDITION);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID,
                "Dune Attack Speed", fullSetAttackSpeedBonus(totals), AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public double fullSetAttackSpeedBonus(Totals totals) {
        if (totals.count < 4) {
            return 0.0D;
        }
        return 0.10D + (Math.max(0, Math.min(totals.minLevel, 4)) * 0.075D);
    }

    public static final class Totals {
        private int count;
        private int minLevel = 99;
        private double speedBonus;
        private double armorReduction;

        public int count() { return count; }
        public int minLevel() { return count > 0 ? minLevel : 0; }
        public double speedBonus() { return speedBonus; }
        public double armorReduction() { return armorReduction; }
    }
}
