package com.xiyue.trimmod.common.trim.set.dune;

import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.entity.DuneTornadoEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class DuneSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.DUNE)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            horizontal = Vec3.directionFromRotation(0.0F, player.getYRot());
        } else {
            horizontal = horizontal.normalize();
        }
        Vec3 spawn = player.position().add(horizontal.scale(2.8D));

        DuneTornadoEntity tornado = EntityInit.DUNE_TORNADO.get().create(level);
        if (tornado == null) {
            return;
        }

        tornado.setPos(spawn.x, player.getY(), spawn.z);
        tornado.setBaseY(player.getY());
        tornado.setLife(100 + (minLevel * 10));
        tornado.setHeightBlocks(4.4F + (minLevel * 0.15F));
        tornado.setEndRadius(1.05F + (minLevel * 0.04F));
        tornado.setMidRadius(0.22F);
        tornado.setTravel(horizontal, 0.095D + (minLevel * 0.005D));
        tornado.setDamagePerHit(1.8F + (minLevel * 0.4F));
        tornado.setHitInterval(10);
        tornado.setSkillLevel(minLevel);
        tornado.setOwnerUuid(player.getUUID());
        level.addFreshEntity(tornado);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 0.9F, 0.65F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.HUSK_AMBIENT, SoundSource.PLAYERS, 0.65F, 0.85F);
        level.sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.15D, player.getZ(),
                14, 0.55D, 0.08D, 0.55D, 0.02D);
        level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                player.getX(), player.getY() + 0.05D, player.getZ(),
                18, 0.65D, 0.04D, 0.65D, 0.01D);

        TrimSkillCooldowns.startCooldown(player, TrimPatterns.DUNE, minLevel);
    }
}
