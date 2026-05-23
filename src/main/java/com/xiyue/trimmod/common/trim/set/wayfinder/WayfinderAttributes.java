package com.xiyue.trimmod.common.trim.set.wayfinder;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class WayfinderAttributes {
    private static final UUID SPEED_UUID = UUID.fromString("0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d");

    public void collect(int level, Totals totals) {
        totals.speedBonus += 0.04D + (0.02D * level);
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.MOVEMENT_SPEED, SPEED_UUID,
                "Wayfinder Speed", totals.speedBonus, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final class Totals {
        private double speedBonus;

        public double speedBonus() { return speedBonus; }
    }
}

