package com.xiyue.trimmod.common.trim.api;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

@FunctionalInterface
public interface TrimModifierApplier {
    void apply(Attribute attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation);
}
