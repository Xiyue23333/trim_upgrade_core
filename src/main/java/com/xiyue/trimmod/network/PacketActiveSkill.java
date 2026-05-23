package com.xiyue.trimmod.network;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.set.contributor.ContributorSet;
import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.silence.SilenceSet;
import com.xiyue.trimmod.common.trim.set.spire.SpireSet;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSet;
import com.xiyue.trimmod.common.trim.set.snout.SnoutSet;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.common.util.HighlightUtils;
import com.xiyue.trimmod.common.util.MarkUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.entity.CoastWaveEntity;
import com.xiyue.trimmod.entity.DuneTornadoEntity;

import java.util.function.Supplier;

public class PacketActiveSkill {
    public PacketActiveSkill() {}

    public PacketActiveSkill(FriendlyByteBuf friendlyByteBuf) {
    }

    public static void encode(PacketActiveSkill msg, FriendlyByteBuf buf) {}

    public static PacketActiveSkill decode(FriendlyByteBuf buf) {
        return new PacketActiveSkill();
    }

    private static Component tr(String key, Object... args) {
        return Component.translatable(key, args);
    }

    private static void showActionBar(ServerPlayer player, Component text) {
        player.displayClientMessage(text, true);
    }

    private static void clearActionBar(ServerPlayer player) {
        player.displayClientMessage(Component.empty(), true);
    }

    public static void handle(PacketActiveSkill msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            // Dispatch the active skill for the equipped full trim set
            if (isContributorSet(player)) {
                ContributorSet.SKILL.activate(player);
            } else if (isSwampSet(player)) {
                SwampSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            } else if (isFullSet(player, TrimPatterns.SPIRE)) {
                SpireSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player),
                        text -> showActionBar(player, text), () -> clearActionBar(player));
            } else if (isFullSet(player, TrimPatterns.EYE)) {
                EyeSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player),
                        text -> showActionBar(player, text), () -> clearActionBar(player));
            } else if (isFullSet(player, TrimPatterns.RIB)) {
                RibSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            } else if (isFullSet(player, TrimPatterns.SILENCE)) {
                SilenceSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            } else if (isFullSet(player, TrimPatterns.TIDE)) {
                handleTideSkill(player);
            } else if (isFullSet(player, TrimPatterns.SNOUT)) {
                handleSnoutSkill(player);
            } else if (isFullSet(player, TrimPatterns.DUNE)) {
                handleDuneSkill(player);
            } else if (isFullSet(player, TrimPatterns.WAYFINDER)) {
                WayfinderSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            } else if (isFullSet(player, TrimPatterns.VEX)) {
                handleVexSkill(player);
            } else if (isFullSet(player, TrimPatterns.COAST)) {
                CoastSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            } else if (isFullSet(player, TrimPatterns.SHAPER)) {
                ShaperSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // Contributor set: WILD + WARD + COAST + TIDE
    private static boolean isContributorSet(ServerPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) return false;

        var trimHead = ArmorTrim.getTrim(player.level().registryAccess(), head);
        var trimChest = ArmorTrim.getTrim(player.level().registryAccess(), chest);
        var trimLegs = ArmorTrim.getTrim(player.level().registryAccess(), legs);
        var trimFeet = ArmorTrim.getTrim(player.level().registryAccess(), feet);

        if (trimHead.isEmpty() || trimChest.isEmpty() || trimLegs.isEmpty() || trimFeet.isEmpty()) return false;

        return trimHead.get().pattern().is(TrimPatterns.WILD)
                && trimChest.get().pattern().is(TrimPatterns.WARD)
                && trimLegs.get().pattern().is(TrimPatterns.COAST)
                && trimFeet.get().pattern().is(TrimPatterns.TIDE);
    }

    private static boolean isSwampSet(ServerPlayer player) {
        int tideCount = 0;
        int wildCount = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) {
                continue;
            }
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty()) {
                continue;
            }
            if (trim.get().pattern().is(TrimPatterns.TIDE)) {
                tideCount++;
            } else if (trim.get().pattern().is(TrimPatterns.WILD)) {
                wildCount++;
            }
        }
        return tideCount >= 2 && wildCount >= 2;
    }

    // Contributor set active skill: Hammer Drop
    private static void handleContributorSkill(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || player.getCooldowns().isOnCooldown(chest.getItem())) return;

        ServerLevel level = player.serverLevel();
        int minLvl = TrimUtils.getMinUpgradeLevel(player);

        // Store airborne state in persistent data; landing is resolved later during tick events
        net.minecraft.nbt.CompoundTag data = player.getPersistentData();
        final String PENDING_KEY = "trimupgrade_contributor_smash_pending";
        final String AIRBORNE_KEY = "trimupgrade_contributor_smash_airborne";
        final String LVL_KEY = "trimupgrade_contributor_smash_lvl";
        final String NOFALL_UNTIL_KEY = "trimupgrade_contributor_smash_nofall_until";
        final String STARTED_KEY = "trimupgrade_contributor_smash_started";

        if (data.getBoolean(PENDING_KEY)) return;

        data.putBoolean(PENDING_KEY, true);
        data.putBoolean(AIRBORNE_KEY, false);
        data.putInt(LVL_KEY, minLvl);
        long now = level.getGameTime();
        data.putLong(STARTED_KEY, now);
        data.putLong(NOFALL_UNTIL_KEY, now + 80L); // Keep a short no-fall window after launch
        // Apply the launch impulse
        Vec3 vel = player.getDeltaMovement();
        player.setDeltaMovement(vel.x, 0.85D, vel.z);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.fallDistance = 0;

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.7F, 1.15F);
        level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1, player.getZ(), 10, 0.35, 0.05, 0.35, 0.02);

        // Start cooldown
        player.getCooldowns().addCooldown(chest.getItem(), Config.cooldownTicks(Config.cooldownContributorSeconds));
    }

    private static void executeEyeSkill(ServerPlayer player) {
        int minLevel = TrimUtils.getMinUpgradeLevel(player);
        int charges = player.getPersistentData().getInt(PersistentDataKeys.EYE_CHARGE_COUNT);

        // Ender energy cost: level 0 = 100, level 4 = 50
        double cost = 100.0 - (12.5 * minLevel);
        // Blink distance: level 0 = 7, level 4 = 13
        double distance = 7.0 + (minLevel * 1.5);

        if (charges >= cost) {
            // Compute a safe blink target
            ServerLevel level = player.serverLevel();
            Vec3 targetPos = computeEyeBlinkTarget(player, distance);

            int newCharge = (int) (charges - cost);
            player.getPersistentData().putInt(PersistentDataKeys.EYE_CHARGE_COUNT, newCharge);
            ModMessages.sendToPlayer(new PacketSyncEyeCharge(newCharge), player);

            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
            player.fallDistance = 0; // Prevent blink from causing fall damage
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1, player.getZ(), 20, 0.2, 0.5, 0.2, 0.1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
            clearActionBar(player);
        } else {
            showActionBar(player, tr("actionbar.trimupgrade.eye_energy_not_enough", (int) cost));
        }
    }

    private static Vec3 computeEyeBlinkTarget(ServerPlayer player, double maxDistance) {
        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 horizontal = new Vec3(lookVec.x, 0.0D, lookVec.z);
        if (horizontal.lengthSqr() < 1.0E-6) return player.position();
        Vec3 dir = horizontal.normalize();

        Vec3 endPos = eyePos.add(dir.scale(maxDistance));
        BlockHitResult hit = level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        double hitDistance;
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 delta = hit.getLocation().subtract(eyePos);
            hitDistance = Math.max(0.0D, delta.dot(dir));
        } else {
            hitDistance = maxDistance;
        }

        // Leave a safety margin when blinking toward a block
        double margin = (player.getBbWidth() / 2.0D) + 0.25D;
        double blinkDistance = hit.getType() == HitResult.Type.BLOCK ? Math.max(0.0D, hitDistance - margin) : hitDistance;

        Vec3 desiredEyePos = eyePos.add(dir.scale(blinkDistance));
        Vec3 startFeetPos = player.position();
        Vec3 desiredFeetPos = new Vec3(desiredEyePos.x, startFeetPos.y, desiredEyePos.z);

        if (isBlinkTargetSafe(level, player, desiredFeetPos)) return desiredFeetPos;

        // If blocked, walk backward along the view ray until a safe position is found
        for (int i = 1; i <= 50; i++) {
            Vec3 candidate = desiredFeetPos.subtract(dir.scale(i * 0.10D));
            if (startFeetPos.distanceTo(candidate) <= 0.01D) break;
            if (isBlinkTargetSafe(level, player, candidate)) return candidate;
        }

        return startFeetPos;
    }

    private static boolean isBlinkTargetSafe(ServerLevel level, ServerPlayer player, Vec3 feetPos) {
        if (feetPos.y < (level.getMinBuildHeight() - 2) || feetPos.y > (level.getMaxBuildHeight() + 2)) return false;

        BlockPos blockPos = BlockPos.containing(feetPos);
        if (!level.getWorldBorder().isWithinBounds(blockPos)) return false;

        AABB movedBox = player.getBoundingBox().move(feetPos.x - player.getX(), feetPos.y - player.getY(), feetPos.z - player.getZ());
        return level.noCollision(player, movedBox);
    }

    private static void executeRibSkill(ServerPlayer player) {
        int minLevel = TrimUtils.getMinUpgradeLevel(player);
        int charges = player.getPersistentData().getInt(PersistentDataKeys.RIB_CHARGE_COUNT);
        if (charges >= 8) {
            player.getPersistentData().putInt(PersistentDataKeys.RIB_CHARGE_COUNT, 0);
            ModMessages.sendToPlayer(new PacketSyncRibCharge(0), player);
            // Access the server level
            ServerLevel level = player.serverLevel();
            // Control duration: base 120 ticks, +30 ticks per level
            int duration = 120 + (minLevel * 30);
            float radius = 5.0f;
            // Damage scales from the target's armor: base 20%, +10% per level
            float armorReduction = 0.2f + (minLevel * 0.1f);

            AABB area = player.getBoundingBox().inflate(radius);
            level.getEntitiesOfClass(LivingEntity.class, area).forEach(target -> {
                if (target != player && target.isAlive()) {
                    // Apply slow and forced airborne control
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4));
                    target.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 200));
                    // Deal magic damage based on current armor with a minimum floor
                    net.minecraft.world.entity.ai.attributes.AttributeInstance armorAttr =
                            target.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR);
                    if (armorAttr != null) {
                        float currentArmor = (float) armorAttr.getValue();
                        // Minimum damage floor: 5
                        target.hurt(player.damageSources().magic(), Math.max(5.0f, currentArmor * armorReduction));
                    }
                    // Spawn hit particles
                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,
                            target.getX(), target.getY() + 1, target.getZ(), 15, 0.2, 0.5, 0.2, 0.02);
                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.WHITE_ASH,
                            target.getX(), target.getY() + 1, target.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
                }
            });

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 1.2f);
        } else {
            showActionBar(player, tr("actionbar.trimupgrade.rib_energy_not_enough", charges, 8));
        }
    }

    private static void handleSilenceSkill(ServerPlayer player) {
        String TAG = PersistentDataKeys.SILENCE_CHARGE_COUNT;
        int charges = player.getPersistentData().getInt(TAG);
        int minLevel = TrimUtils.getMinUpgradeLevel(player);
        // Requires 4 Echo Energy
        if (charges < 4) {
            showActionBar(player, tr("actionbar.trimupgrade.silence_energy_not_enough", 4));
            return;
        }

        if (player.isShiftKeyDown()) {
            // Branch A: Silence Field
            executeSilenceField(player, minLevel);
        } else {
            // Branch B: Ancient Echo
            executeSonicBoom(player, minLevel);
        }
        player.getPersistentData().putInt(PersistentDataKeys.SILENCE_CHARGE_COUNT, 0);
        ModMessages.sendToPlayer(new PacketSyncSilenceCharge(0), player);
    }
    private static void executeSilenceField(ServerPlayer player, int minLevel) {
        ServerLevel level = player.serverLevel();
        double radius = 3.0D + minLevel;

        for (double d = 0.0D; d <= Math.PI * 2.0D; d += Math.PI / 8.0D) {
            double xOffset = Math.cos(d) * radius;
            double zOffset = Math.sin(d) * radius;
            level.sendParticles(ParticleTypes.SONIC_BOOM,
                    player.getX() + xOffset, player.getY() + 1.0D, player.getZ() + zOffset,
                    1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.SCULK_SOUL,
                    player.getX() + xOffset * 0.5D, player.getY() + 0.1D, player.getZ() + zOffset * 0.5D,
                    2, 0.1D, 0.1D, 0.1D, 0.02D);
        }

        level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius)).forEach(target -> {
            if (target != player) {
                target.hurt(player.damageSources().magic(), player.getArmorValue() * (0.35f + minLevel * 0.1f));
                double slow = -(0.45D + (minLevel * 0.05D));
                MarkUtils.applySilenceHighlightSlow(target, level, slow, 100);
                target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
                level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                        target.getX(), target.getY() + 1.0D, target.getZ(), 10, 0.2D, 0.2D, 0.2D, 0.05D);
            }
        });

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.5F, 1.0F);
    }

    private static void executeSonicBoom(ServerPlayer player, int minLevel) {
        ServerLevel level = player.serverLevel();
        float damage = player.getArmorValue() * (0.25f + minLevel * 0.075f);
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        double maxRange = 16.0D;
        Vec3 endPos = eyePos.add(lookVec.scale(maxRange));
        double step = 0.8D;

        for (double d = 0.0D; d < maxRange; d += step) {
            Vec3 pos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }

        level.sendParticles(ParticleTypes.SONIC_BOOM, endPos.x, endPos.y, endPos.z, 1, 0, 0, 0, 0);

        AABB beamArea = new AABB(eyePos, endPos).inflate(1.2D);
        level.getEntitiesOfClass(LivingEntity.class, beamArea, e -> e != player).forEach(target -> {
            if (target.getBoundingBox().inflate(0.5D).clip(eyePos, endPos).isPresent()) {
                target.hurt(player.damageSources().magic(), damage);
                HighlightUtils.applyColoredHighlight(target, level, "tu_hl_si", ChatFormatting.DARK_AQUA, 100);
                double slow = -(0.45D + (minLevel * 0.05D));
                MarkUtils.applySilenceHighlightSlow(target, level, slow, 100);
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        target.getX(), target.getY() + 1.0D, target.getZ(), 1, 0, 0, 0, 0);
            }
        });

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0F, 1.0F);
        player.getPersistentData().putInt(PersistentDataKeys.SILENCE_CHARGE_COUNT, 0);
        ModMessages.sendToPlayer(new PacketSyncSilenceCharge(0), player);
    }

    private static void handleShaperSkill(ServerPlayer player) {
        if (isFullSet(player, TrimPatterns.SHAPER)) {
            if (player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER) > 0) {
                return;
            }

            int energy = player.getPersistentData().getInt(PersistentDataKeys.SHAPER_ENERGY);
            if (energy < 100) {
                return;
            }

            player.getPersistentData().putInt(PersistentDataKeys.SHAPER_ENERGY, 0);
            player.getPersistentData().putInt(PersistentDataKeys.SHAPER_REGEN_TICKS, 0);
            ModMessages.sendToPlayer(new PacketSyncShaperEnergy(0), player);

            int lvl = TrimUtils.getMinUpgradeLevel(player);
            int duration = 240 + (lvl * 40);

            player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_TIMER, duration);
            player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_MAX_TIMER, duration);
            ModMessages.sendToPlayer(new PacketSyncShaperSkillTime(duration, duration), player);
            ServerLevel level = player.serverLevel();
            for (int i = 0; i < 360; i += 15) {
                double rad = Math.toRadians(i);
                level.sendParticles(ParticleTypes.CLOUD,
                        player.getX() + Math.cos(rad) * 0.5, player.getY() + 0.1, player.getZ() + Math.sin(rad) * 0.5,
                        1, 0, 0, 0, 0.05); // Ring particles indicate the barrier is active
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
        }
    }

    private static void handleCoastSkill(ServerPlayer player) {
        CoastSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
    }

    private static void handleTideSkill(ServerPlayer player) {
        TideSet.SKILL.activate(player, text -> showActionBar(player, text), () -> clearActionBar(player));
    }


        private static void handleSnoutSkill(ServerPlayer player) {
        SnoutSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
    }
    private static void handleDuneSkill(ServerPlayer player) {
        DuneSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
    }

    private static void handleWayfinderSkill(ServerPlayer player) {
        WayfinderSet.SKILL.activate(player, TrimUtils.getMinUpgradeLevel(player), text -> showActionBar(player, text));
    }

    // Vex set active skill
    private static void handleVexSkill(ServerPlayer player) {
        int minLvl = TrimUtils.getMinUpgradeLevel(player);
        VexSet.SKILL.activate(player, minLvl, text -> showActionBar(player, text));
    }


    private static boolean isFullSet(ServerPlayer player, ResourceKey<TrimPattern> pattern) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty() || !trim.get().pattern().is(pattern)) {
                return false;
            }
        }
        return true;
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {}
}



