package com.xiyue.trimmod.common.trim.set.eye;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncEyeCharge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class EyeSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar, Runnable clearActionBar) {
        int charges = player.getPersistentData().getInt(PersistentDataKeys.EYE_CHARGE_COUNT);
        double cost = 100.0D - (12.5D * minLevel);
        double distance = 7.0D + (minLevel * 1.5D);

        if (charges < cost) {
            actionBar.accept(Component.translatable("actionbar.trimupgrade.eye_energy_not_enough", (int) cost));
            return;
        }

        ServerLevel level = player.serverLevel();
        Vec3 targetPos = computeBlinkTarget(player, distance);

        int newCharge = (int) (charges - cost);
        player.getPersistentData().putInt(PersistentDataKeys.EYE_CHARGE_COUNT, newCharge);
        ModMessages.sendToPlayer(new PacketSyncEyeCharge(newCharge), player);

        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        player.fallDistance = 0;
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1, player.getZ(), 20, 0.2, 0.5, 0.2, 0.1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
        clearActionBar.run();
    }

    private Vec3 computeBlinkTarget(ServerPlayer player, double maxDistance) {
        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(lookVec.x, 0.0D, lookVec.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            return player.position();
        }
        Vec3 direction = horizontal.normalize();

        Vec3 endPos = eyePos.add(direction.scale(maxDistance));
        BlockHitResult hit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        double hitDistance;
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 delta = hit.getLocation().subtract(eyePos);
            hitDistance = Math.max(0.0D, delta.dot(direction));
        } else {
            hitDistance = maxDistance;
        }

        double margin = (player.getBbWidth() / 2.0D) + 0.25D;
        double blinkDistance = hit.getType() == HitResult.Type.BLOCK ? Math.max(0.0D, hitDistance - margin) : hitDistance;

        Vec3 desiredEyePos = eyePos.add(direction.scale(blinkDistance));
        Vec3 startFeetPos = player.position();
        Vec3 desiredFeetPos = new Vec3(desiredEyePos.x, startFeetPos.y, desiredEyePos.z);

        if (isTargetSafe(level, player, desiredFeetPos)) {
            return desiredFeetPos;
        }

        for (int i = 1; i <= 50; i++) {
            Vec3 candidate = desiredFeetPos.subtract(direction.scale(i * 0.10D));
            if (startFeetPos.distanceTo(candidate) <= 0.01D) {
                break;
            }
            if (isTargetSafe(level, player, candidate)) {
                return candidate;
            }
        }

        return startFeetPos;
    }

    private boolean isTargetSafe(ServerLevel level, ServerPlayer player, Vec3 feetPos) {
        if (feetPos.y < (level.getMinBuildHeight() - 2) || feetPos.y > (level.getMaxBuildHeight() + 2)) {
            return false;
        }
        if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(feetPos))) {
            return false;
        }
        AABB moved = player.getBoundingBox().move(feetPos.x - player.getX(), feetPos.y - player.getY(), feetPos.z - player.getZ());
        return level.noCollision(player, moved);
    }
}


