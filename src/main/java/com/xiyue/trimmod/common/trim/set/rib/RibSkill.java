package com.xiyue.trimmod.common.trim.set.rib;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncRibCharge;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public final class RibSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        int charges = player.getPersistentData().getInt(PersistentDataKeys.RIB_CHARGE_COUNT);
        if (charges < 8) {
            actionBar.accept(Component.translatable("actionbar.trimupgrade.rib_energy_not_enough", charges, 8));
            return;
        }

        player.getPersistentData().putInt(PersistentDataKeys.RIB_CHARGE_COUNT, 0);
        ModMessages.sendToPlayer(new PacketSyncRibCharge(0), player);

        ServerLevel level = player.serverLevel();
        int duration = 120 + (minLevel * 30);
        float radius = 5.0f;
        float armorReduction = 0.2f + (minLevel * 0.1f);

        AABB area = player.getBoundingBox().inflate(radius);
        level.getEntitiesOfClass(LivingEntity.class, area).forEach(target -> {
            if (target == player || !target.isAlive()) {
                return;
            }
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4));
            target.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 200));

            AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
            if (armor != null) {
                float currentArmor = (float) armor.getValue();
                target.hurt(player.damageSources().magic(), Math.max(5.0f, currentArmor * armorReduction));
            }

            level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1, target.getZ(), 15, 0.2, 0.5, 0.2, 0.02);
            level.sendParticles(ParticleTypes.WHITE_ASH, target.getX(), target.getY() + 1, target.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
        });

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 1.2f);
    }
}


