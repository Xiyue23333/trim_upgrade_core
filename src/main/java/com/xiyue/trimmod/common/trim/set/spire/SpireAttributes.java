package com.xiyue.trimmod.common.trim.set.spire;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public final class SpireAttributes {
    private static final UUID RANGE_UUID = UUID.fromString("7f3e1a2b-4c5d-6e7f-8a9b-0c1d2e3f4a5b");
    private static final UUID DAMAGE_UUID = UUID.fromString("9b3c0a11-1d3a-4d55-bb5c-2c5a5ea1b1ad");

    public void collect(int level, Totals totals) {
        totals.damageBonus += 0.02D + (0.015D * level);
        totals.reachBonus += 0.05D + (0.05D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, DAMAGE_UUID,
                "Spire Damage", totals.damageBonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applier.apply(ForgeMod.ENTITY_REACH.get(), RANGE_UUID,
                "Spire Entity Reach", totals.reachBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(ForgeMod.BLOCK_REACH.get(), RANGE_UUID,
                "Spire Block Reach", totals.reachBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double damageBonus;
        private double reachBonus;

        public double damageBonus() { return damageBonus; }
        public double reachBonus() { return reachBonus; }
    }
}
