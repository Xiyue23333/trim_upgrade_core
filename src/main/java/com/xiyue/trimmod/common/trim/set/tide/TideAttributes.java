package com.xiyue.trimmod.common.trim.set.tide;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class TideAttributes {
    private static final UUID HP_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
    private static final UUID SPEED_UUID = UUID.fromString("b2c3d4e5-f6a7-4b6c-9d0e-1f2a3b4c5d6e");
    private static final UUID DAMAGE_UUID = UUID.fromString("c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f");

    public void collect(int level, Totals totals) {
        totals.count++;
        totals.minLevel = Math.min(totals.minLevel, level);
        totals.hpBonus += 1.5D + 1.125D * level;
        totals.speedBonus += 0.04D + 0.0125D * level;
    }

    public void apply(Player player, Totals totals, boolean isDaytime, TrimModifierApplier applier) {
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, HP_UUID, "Tide HP", totals.hpBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, SPEED_UUID, "Tide Speed", totals.speedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        applier.apply(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, DAMAGE_UUID, "Tide Damage", fullSetDamageBonus(totals, isDaytime), AttributeModifier.Operation.ADDITION);
    }

    public double fullSetDamageBonus(Totals totals, boolean isDaytime) {
        if (!isDaytime || totals.count < 4) {
            return 0.0D;
        }
        return 1.0D + 0.5D * Math.max(0, Math.min(totals.minLevel, 4));
    }

    public static final class Totals {
        private int count;
        private int minLevel = 99;
        private double hpBonus;
        private double speedBonus;

        public int count() { return count; }
        public int minLevel() { return count > 0 ? minLevel : 0; }
        public double hpBonus() { return hpBonus; }
        public double speedBonus() { return speedBonus; }
    }
}
