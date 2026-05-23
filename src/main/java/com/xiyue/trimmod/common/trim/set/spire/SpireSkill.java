package com.xiyue.trimmod.common.trim.set.spire;

import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class SpireSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar, Runnable clearActionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.SPIRE)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();
        Vec3 center = player.getEyePosition().add(look.scale(12.0D));

        net.minecraft.world.entity.Display.ItemDisplay blackHole =
                new net.minecraft.world.entity.Display.ItemDisplay(net.minecraft.world.entity.EntityType.ITEM_DISPLAY, level);
        blackHole.setPos(center.x, center.y, center.z);
        blackHole.setCustomName(Component.literal("SpireBlackHole"));
        blackHole.setCustomNameVisible(false);
        blackHole.addTag("SpireBlackHole");
        var data = blackHole.getPersistentData();
        data.putInt("BHAge", 0);
        data.putInt("BHLvl", minLevel);
        data.putUUID("BHOwner", player.getUUID());

        level.addFreshEntity(blackHole);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        TrimSkillCooldowns.startCooldown(player, TrimPatterns.SPIRE, minLevel);
        clearActionBar.run();
    }
}


