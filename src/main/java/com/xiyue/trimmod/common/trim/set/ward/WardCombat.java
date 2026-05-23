package com.xiyue.trimmod.common.trim.set.ward;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class WardCombat {
    public static final String CHARGE_TAG = "ward_charge_count";
    private static final long CHARGE_INTERVAL_TICKS = 15L; // 0.75 seconds

    public void onDefenderHurt(Player player, LivingHurtEvent event, boolean hasFullSet, int minLevel) {
        if (!hasFullSet) {
            return;
        }

        if (player.isShiftKeyDown()) {
            float reduction = 0.14f + (minLevel * 0.08f);
            event.setAmount(event.getAmount() * (1 - reduction));
        }

        long now = player.level().getGameTime();
        long lastChargeTick = player.getPersistentData().getLong(PersistentDataKeys.WARD_LAST_CHARGE_TICK);
        if (lastChargeTick > 0L && now - lastChargeTick < CHARGE_INTERVAL_TICKS) {
            return;
        }

        int charges = player.getPersistentData().getInt(CHARGE_TAG) + 1;
        player.getPersistentData().putLong(PersistentDataKeys.WARD_LAST_CHARGE_TICK, now);
        if (charges >= 5) {
            event.setAmount(event.getAmount() * 0.5f);
            if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {
                float armorValue = player.getArmorValue();
                float pulseDamage = armorValue * (0.24f + (minLevel * 0.06f));
                double range = 3.0D;

                for (int i = 0; i < 360; i += 45) {
                    double rad = Math.toRadians(i);
                    double xOffset = Math.cos(rad) * range;
                    double zOffset = Math.sin(rad) * range;

                    serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                            player.getX() + xOffset, player.getY() + 0.15, player.getZ() + zOffset,
                            1, 0.05, 0.02, 0.05, 0.0);
                    if (i % 90 == 0) {
                        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                                player.getX() + xOffset, player.getY() + 1.0, player.getZ() + zOffset,
                                1, 0, 0, 0, 0);
                    }
                }
                serverLevel.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                        player.getX(), player.getY() + 0.2, player.getZ(),
                        8, 0.4, 0.05, 0.4, 0.01);

                player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range)).forEach(target -> {
                    if (target == player) {
                        return;
                    }
                    double d0 = target.getX() - player.getX();
                    double d1 = target.getZ() - player.getZ();
                    double distance = Math.sqrt(d0 * d0 + d1 * d1);
                    if (distance > 0) {
                        target.knockback(0.25F, -d0, -d1);
                    } else {
                        target.knockback(1.0F, 1.0, 0.0);
                    }
                    target.hurt(player.damageSources().magic(), pulseDamage);
                });
            }
            player.getPersistentData().putInt(CHARGE_TAG, 0);
            return;
        }

        player.getPersistentData().putInt(CHARGE_TAG, charges);
    }
}
