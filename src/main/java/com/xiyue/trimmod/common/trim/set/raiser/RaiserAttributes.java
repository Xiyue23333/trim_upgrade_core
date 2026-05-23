package com.xiyue.trimmod.common.trim.set.raiser;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class RaiserAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("46248cc2-e0ef-4630-9d9f-c14370f99140");

    public void collect(int level, Totals totals) {
        totals.count++;
        totals.minLevel = Math.min(totals.minLevel, level);
        totals.armorBonus += 1.0D + 0.375D * level;
        totals.attackSpeedBonus += 0.04D + 0.01D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_UUID, "Raiser Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Raiser Attack Speed", totals.attackSpeedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final class Totals {
        private int count;
        private int minLevel = 99;
        private double armorBonus;
        private double attackSpeedBonus;

        public int count() { return count; }
        public int minLevel() { return count > 0 ? minLevel : 0; }
        public double armorBonus() { return armorBonus; }
        public double attackSpeedBonus() { return attackSpeedBonus; }
    }
}
