package com.xiyue.trimmod.common.trim.set.rib;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class RibAttributes {
    private static final UUID ARMOR_UUID = UUID.fromString("bc4dbdb7-374c-470a-85c9-2ca62edf129c");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("f6c760ad-d251-4a04-865f-9e931e4c5ef7");
    private static final UUID DAMAGE_UUID = UUID.fromString("f470a16c-3832-443b-8c88-29479634d31d");

    public void collect(int level, Totals totals) {
        totals.armorBonus += (1.0D + (0.5D * level));
        totals.toughnessBonus += (0.4D + (0.1125D * level));
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.ARMOR, ARMOR_UUID,
                "Rib Armor", totals.armorBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID,
                "Rib Toughness", totals.toughnessBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.ATTACK_DAMAGE, DAMAGE_UUID,
                "Rib Damage", totals.damageBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double armorBonus;
        private double toughnessBonus;
        private double damageBonus;

        public double armorBonus() { return armorBonus; }
        public double toughnessBonus() { return toughnessBonus; }
        public double damageBonus() { return damageBonus; }
    }
}
