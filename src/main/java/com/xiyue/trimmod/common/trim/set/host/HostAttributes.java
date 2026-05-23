package com.xiyue.trimmod.common.trim.set.host;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class HostAttributes {
    private static final UUID HEALTH_UUID = UUID.fromString("78a23c10-5678-1234-abcd-999988887777");
    private static final UUID KNOCKBACK_UUID = UUID.fromString("78a23c10-5678-1234-abcd-999988886666");

    public void collect(int level, Totals totals) {
        totals.healthBonus += 1.0D + 0.5D * level;
        totals.knockbackBonus += 0.05D + 0.03D * level;
    }

    public void apply(Player player, Totals totals, TrimModifierApplier applier) {
        applier.apply(Attributes.MAX_HEALTH, HEALTH_UUID, "Host HP", totals.healthBonus, AttributeModifier.Operation.ADDITION);
        applier.apply(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_UUID, "Host KB", totals.knockbackBonus, AttributeModifier.Operation.ADDITION);
    }

    public static final class Totals {
        private double healthBonus;
        private double knockbackBonus;

        public double healthBonus() { return healthBonus; }
        public double knockbackBonus() { return knockbackBonus; }
    }
}
