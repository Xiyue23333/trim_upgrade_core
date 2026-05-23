package com.xiyue.trimmod.common.trim.set.ward;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class WardAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("6be1195d-1FEF-46C3-BEA5-83E9DF1A38A6");
    private static final UUID KNOCKBACK_UUID = UUID.fromString("d0025f2f-492a-44d4-8f3c-43ee40d32da5");

    public void collect(int level, Totals totals) {
        totals.count++;
        totals.minLevel = Math.min(totals.minLevel, level);
        totals.armorBonus += 1.0D + 0.4375D * level;
        totals.knockbackBonus += 0.05D + 0.03D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_UUID, "Ward Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_UUID, "Ward Knockback", totals.knockbackBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private int count;
        private int minLevel = 99;
        private double armorBonus;
        private double knockbackBonus;

        public int count() { return count; }
        public int minLevel() { return count > 0 ? minLevel : 0; }
        public double armorBonus() { return armorBonus; }
        public double knockbackBonus() { return knockbackBonus; }
    }
}
