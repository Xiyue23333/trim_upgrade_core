package com.xiyue.trimmod.common.trim.set.spire;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncSpireEnergy;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.UUID;

public final class SpireTick {
    private static final String ENERGY_TAG = PersistentDataKeys.SPIRE_ENERGY;
    private static final String TICK_TAG = PersistentDataKeys.SPIRE_REGEN_TICKS;
    private static final String PRIDE_KEY = "spire_pride_bonus";
    private static final UUID SPIRE_DAMAGE_UUID = UUID.fromString("9b3c0a11-1d3a-4d55-bb5c-2c5a5ea1b1ad");
    private static final DustParticleOptions SPIRE_OUTER_RING_DUST =
            new DustParticleOptions(new Vector3f(0.35F, 0.14F, 0.58F), 1.15F); // 深紫
    private static final DustParticleOptions SPIRE_INNER_RING_DUST =
            new DustParticleOptions(new Vector3f(0.90F, 0.84F, 1.00F), 1.05F); // 浅紫偏白

    public void tickEnergy(ServerPlayer player, boolean hasFullSet) {
        int energy = player.getPersistentData().getInt(ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Config.cooldownTicks(Config.cooldownSpireSeconds);
            if (fullRechargeTicks <= 0) {
                if (energy < 100) {
                    player.getPersistentData().putInt(ENERGY_TAG, 100);
                    player.getPersistentData().putInt(TICK_TAG, 0);
                    ModMessages.sendToPlayer(new PacketSyncSpireEnergy(100), player);
                }
            } else if (energy < 100) {
                int ticks = player.getPersistentData().getInt(TICK_TAG) + 1;
                int intervalTicks = Math.max(1, fullRechargeTicks / 10);
                if (ticks >= intervalTicks) {
                    int newEnergy = Math.min(100, energy + 10);
                    player.getPersistentData().putInt(ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncSpireEnergy(newEnergy), player);
                    ticks = 0;
                }
                player.getPersistentData().putInt(TICK_TAG, ticks);
            }
            return;
        }

        if (energy > 0) {
            player.getPersistentData().putInt(ENERGY_TAG, 0);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncSpireEnergy(0), player);
        } else if (player.getPersistentData().getInt(TICK_TAG) > 0) {
            player.getPersistentData().putInt(TICK_TAG, 0);
        }
    }

    public void tickPride(ServerPlayer player, boolean hasFullSet) {
        if (!hasFullSet) {
            player.getPersistentData().remove(PRIDE_KEY);
            return;
        }

        if (player.tickCount % 10 == 0) {
            int count = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(8.0D),
                    entity -> entity.isAlive() && entity != player && !(entity instanceof net.minecraft.world.entity.player.Player)).size();
            int capped = Math.min(count, 16);
            int stacks = capped / 4;
            player.getPersistentData().putDouble(PRIDE_KEY, stacks * 1.5D);
        }
    }

    public void tickAttackBonus(ServerPlayer player, boolean hasAnySpirePiece) {
        var attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack == null) {
            return;
        }

        attack.removeModifier(SPIRE_DAMAGE_UUID);
        if (!hasAnySpirePiece) {
            return;
        }

        double bonus = 0.0D;
        for (ItemStack armor : player.getArmorSlots()) {
            if (!TrimUtils.isTrim(player, armor, TrimPatterns.SPIRE)) {
                continue;
            }
            int level = TrimUtils.getUpgradeLevel(armor);
            bonus += 0.02D + (0.015D * level);
        }

        if (bonus != 0.0D) {
            attack.addTransientModifier(new AttributeModifier(
                    SPIRE_DAMAGE_UUID, "Spire Damage", bonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    public void tickMajestyAura(ServerPlayer player, boolean hasFullSet, int minLevel) {
        if (!hasFullSet || player.tickCount % 5 != 0) {
            return;
        }

        renderRangeRings(player);

        double slowAmount = -(0.1D + (minLevel * 0.025D));
        long now = player.level().getGameTime();
        long until = now + 30L;

        AABB area = player.getBoundingBox().inflate(5.0D);
        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && !(entity instanceof net.minecraft.world.entity.player.Player))) {
            var speed = target.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) {
                continue;
            }
            speed.removeModifier(SpireSetConstants.MAJESTY_SLOW_UUID);
            speed.addTransientModifier(new AttributeModifier(
                    SpireSetConstants.MAJESTY_SLOW_UUID, "Spire Majesty Slowdown", slowAmount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            target.getPersistentData().putLong(SpireSetConstants.MAJESTY_SLOW_UNTIL_KEY, until);
        }
    }

    private void renderRangeRings(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        double y = player.getBoundingBox().minY - 0.02D;
        spawnRing(level, player.getX(), y, player.getZ(), 8.0D, 36, SPIRE_OUTER_RING_DUST);
        spawnRing(level, player.getX(), y, player.getZ(), 5.0D, 24, SPIRE_INNER_RING_DUST);
    }

    private void spawnRing(ServerLevel level, double cx, double y, double cz,
                           double radius, int points, DustParticleOptions particle) {
        double step = (Math.PI * 2.0D) / points;
        for (int i = 0; i < points; i++) {
            double angle = step * i;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            level.sendParticles(particle, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}


