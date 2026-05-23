package com.xiyue.trimmod.common.trim.set.contributor;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ContributorSkill {
    public static final String PENDING_KEY = "trimupgrade_contributor_smash_pending";
    public static final String AIRBORNE_KEY = "trimupgrade_contributor_smash_airborne";
    public static final String LEVEL_KEY = "trimupgrade_contributor_smash_lvl";
    public static final String NOFALL_UNTIL_KEY = "trimupgrade_contributor_smash_nofall_until";
    public static final String STARTED_KEY = "trimupgrade_contributor_smash_started";

    public void activate(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || player.getCooldowns().isOnCooldown(chest.getItem())) {
            return;
        }

        ServerLevel level = player.serverLevel();
        int minLevel = TrimUtils.getMinUpgradeLevel(player);
        var data = player.getPersistentData();

        if (data.getBoolean(PENDING_KEY)) {
            return;
        }

        data.putBoolean(PENDING_KEY, true);
        data.putBoolean(AIRBORNE_KEY, false);
        data.putInt(LEVEL_KEY, minLevel);
        long now = level.getGameTime();
        data.putLong(STARTED_KEY, now);
        data.putLong(NOFALL_UNTIL_KEY, now + 80L);

        Vec3 velocity = player.getDeltaMovement();
        player.setDeltaMovement(velocity.x, 0.85D, velocity.z);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.fallDistance = 0;

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.7F, 1.15F);
        level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1D, player.getZ(),
                10, 0.35D, 0.05D, 0.35D, 0.02D);

        player.getCooldowns().addCooldown(chest.getItem(), Config.cooldownTicks(Config.cooldownContributorSeconds));
    }
}
