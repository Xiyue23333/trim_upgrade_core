package com.xiyue.trimmod.common.trim.set.vex;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class VexAttributes {
    private static final UUID HEALTH_UUID = UUID.fromString("7a8b9c0d-e1f2-3a4b-5c6d-7e8f9a0b1c2d");
    private static final UUID DAMAGE_UUID = UUID.fromString("8b9c0d1e-f2a3-4b5c-6d7e-8f9a0b1c2d3e");

    public void collect(int level, Totals totals) {
        totals.healthPenalty -= 0.05D + 0.0125D * level;
        totals.damageBonus += 0.05D + 0.0125D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, HEALTH_UUID, "Vex HP Penalty", totals.healthPenalty, AttributeModifier.Operation.MULTIPLY_BASE);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, DAMAGE_UUID, "Vex Damage Bonus", totals.damageBonus, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final class Totals {
        private double healthPenalty;
        private double damageBonus;

        public double healthPenalty() { return healthPenalty; }
        public double damageBonus() { return damageBonus; }
    }
}
