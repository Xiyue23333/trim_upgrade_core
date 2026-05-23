package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public final class TrimLivingAttackDispatcher {
    private TrimLivingAttackDispatcher() {
    }

    public static void dispatch(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        EyeSet.COMBAT.onDefenderAttacked(event, player);
        if (event.isCanceled()) return;

        int wallTimer = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER);
        if (wallTimer <= 0) {
            return;
        }

        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) {
            return;
        }

        event.setCanceled(true);
        Vec3 motion = projectile.getDeltaMovement();
        projectile.setDeltaMovement(motion.scale(-1.2));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.5F);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    projectile.getX(), projectile.getY(), projectile.getZ(), 5, 0.2, 0.2, 0.2, 0.2);
        }
    }
}


