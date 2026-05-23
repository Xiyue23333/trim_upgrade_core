package com.xiyue.trimmod.common.event;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.effect.SoulSacrificeEffect;
import com.xiyue.trimmod.common.trim.context.PlayerTrimContextFactory;
import com.xiyue.trimmod.common.trim.dispatcher.TrimAttributeDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimCombatDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimCriticalHitDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimEffectApplicableDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimLivingAttackDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimLivingHurtDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimPlayerTickDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimTargetChangeDispatcher;
import com.xiyue.trimmod.common.trim.dispatcher.TrimTickDispatcher;
import com.xiyue.trimmod.common.trim.set.coast.CoastAttributes;
import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.set.contributor.ContributorAttributes;
import com.xiyue.trimmod.common.trim.set.contributor.ContributorSet;
import com.xiyue.trimmod.common.trim.set.dune.DuneAttributes;
import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeAttributes;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.host.HostAttributes;
import com.xiyue.trimmod.common.trim.set.host.HostSet;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserAttributes;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserSet;
import com.xiyue.trimmod.common.trim.set.rib.RibAttributes;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.sentry.SentryAttributes;
import com.xiyue.trimmod.common.trim.set.sentry.SentrySet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperAttributes;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.silence.SilenceAttributes;
import com.xiyue.trimmod.common.trim.set.silence.SilenceSet;
import com.xiyue.trimmod.common.trim.set.snout.SnoutAttributes;
import com.xiyue.trimmod.common.trim.set.snout.SnoutSet;
import com.xiyue.trimmod.common.trim.set.spire.SpireSet;
import com.xiyue.trimmod.common.trim.set.tide.TideAttributes;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.swamp.SwampAttributes;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSet;
import com.xiyue.trimmod.common.trim.set.vex.VexAttributes;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.ward.WardAttributes;
import com.xiyue.trimmod.common.trim.set.ward.WardSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderAttributes;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import com.xiyue.trimmod.common.trim.set.wild.WildAttributes;
import com.xiyue.trimmod.common.trim.set.wild.WildSet;
import com.xiyue.trimmod.common.registry.ModParticles;
import com.xiyue.trimmod.core.init.ModEffects;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.common.util.HighlightUtils;
import com.xiyue.trimmod.common.util.MarkUtils;
import com.xiyue.trimmod.network.*;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

import static net.minecraft.world.item.armortrim.TrimPatterns.RAISER;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class TrimAttributeEventHandler {

    // Central event handler for trim combat, attributes, and runtime skill states.







    // ===== Attribute modifier UUIDs grouped by trim set =====
    private static final UUID TIDE_HP_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
    private static final UUID TIDE_SP_UUID = UUID.fromString("b2c3d4e5-f6a7-4b6c-9d0e-1f2a3b4c5d6e");
    private static final UUID TIDE_DAMAGE_UUID = UUID.fromString("c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f");



    // Wild set modifiers.
    private static final UUID WILD_ARMOR_UUID = UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f");
    private static final UUID WILD_TOUGHNESS_UUID = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a");



    // Snout set modifiers.
    private static final UUID SNOUT_ARMOR_UUID = UUID.fromString("e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b");
    private static final UUID SNOUT_KNOCKBACK_UUID = UUID.fromString("f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c");




    // Vex set modifiers.
    private static final UUID VEX_HEALTH_UUID = UUID.fromString("7a8b9c0d-e1f2-3a4b-5c6d-7e8f9a0b1c2d");
    private static final UUID VEX_DAMAGE_UUID = UUID.fromString("8b9c0d1e-f2a3-4b5c-6d7e-8f9a0b1c2d3e");



    // Dune set modifiers.
    private static final UUID DUNE_SPEED_UUID = UUID.fromString("d1e2f3a4-b5c6-4d7e-8f9a-0b1c2d3e4f5a");
    private static final UUID DUNE_ARMOR_UUID = UUID.fromString("e2f3a4b5-c6d7-4e8f-9a0b-1c2d3e4f5a6b");
    private static final UUID DUNE_ATTACK_SPEED_UUID = UUID.fromString("f3a4b5c6-d7e8-4f9a-0b1c-2d3e4f5a6b7c");



    // Host set modifiers.
    private static final UUID HOST_HEALTH_UUID = UUID.fromString("78a23c10-5678-1234-abcd-999988887777");
    private static final UUID HOST_KB_UUID = UUID.fromString("78a23c10-5678-1234-abcd-999988886666");

    // Swamp set modifiers.
    private static final UUID SWAMP_HP_UUID = UUID.fromString("5d2e0f41-5b8a-4f2f-8a5b-4f3d7c9b2f11");
    private static final UUID SWAMP_SPEED_UUID = UUID.fromString("7d8f1b52-2a7e-4d31-9d2a-3abf2f8c4c22");
    private static final UUID SWAMP_ARMOR_UUID = UUID.fromString("9c3a6e11-1f4d-4e90-9f35-5b9d18b6f333");
    private static final UUID SWAMP_TOUGHNESS_UUID = UUID.fromString("a4b5c6d7-e8f9-4a1b-9c2d-3e4f50617283");




    // Sentry set modifiers.
    private static final UUID SENTRY_SPEED_UUID = UUID.fromString("5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0d");
    private static final UUID SENTRY_TOUGHNESS_UUID = UUID.fromString("6b7c8d9e-0f1a-2b3c-4d5e-6f7a8b9c0d1e");



    // Shaper set (Iron Wall) modifiers.
    private static final UUID SHAPER_KNOCKBACK_RES_UUID = UUID.fromString("3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f");
    private static final UUID SHAPER_SLOW_UUID = UUID.fromString("5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b");



    // Raiser set modifiers.
    private static final UUID RAISER_ARMOR_UUID = UUID.fromString("4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a");
    private static final UUID RAISER_ATTACK_SPEED_UUID = UUID.fromString("46248cc2-e0ef-4630-9d9f-c14370f99140");



    // Ward set modifiers.
    private static final UUID WARD_ARMOR_UUID = UUID.fromString("6be1195d-1FEF-46C3-BEA5-83E9DF1A38A6");
    private static final UUID WARD_KNOCKBACK_UUID = UUID.fromString("d0025f2f-492a-44d4-8f3c-43ee40d32da5");






    // Spire set modifiers and slow-state key.
    private static final UUID SPIRE_RANGE_UUID = UUID.fromString("7f3e1a2b-4c5d-6e7f-8a9b-0c1d2e3f4a5b");
    private static final UUID SPIRE_DAMAGE_UUID = UUID.fromString("9b3c0a11-1d3a-4d55-bb5c-2c5a5ea1b1ad");
    private static final UUID SPIRE_PRIDE_DAMAGE_UUID = UUID.fromString("6c0e53d3-1f5e-4a84-8db1-8d50cc2b9ed9");
    private static final UUID SPIRE_MAJESTY_SLOW_UUID = UUID.fromString("d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a");
    private static final String SPIRE_MAJESTY_SLOW_UNTIL_KEY = "trimupgrade_spire_majesty_slow_until";
    private static final String SPIRE_BH_LIGHT_X_KEY = "BHLightX";
    private static final String SPIRE_BH_LIGHT_Y_KEY = "BHLightY";
    private static final String SPIRE_BH_LIGHT_Z_KEY = "BHLightZ";




    // Contributor and Tide active-skill runtime keys.
    private static final UUID CONTRIBUTOR_CROUCH_KB_UUID = UUID.fromString("c3a8c9c5-2a74-4d7b-8b6b-3bcae62b9855");
    private static final String CONTRIBUTOR_SMASH_PENDING_KEY = "trimupgrade_contributor_smash_pending";
    private static final String CONTRIBUTOR_SMASH_AIRBORNE_KEY = "trimupgrade_contributor_smash_airborne";
    private static final String CONTRIBUTOR_SMASH_LVL_KEY = "trimupgrade_contributor_smash_lvl";
    private static final String CONTRIBUTOR_SMASH_NOFALL_UNTIL_KEY = "trimupgrade_contributor_smash_nofall_until";
    private static final String CONTRIBUTOR_SMASH_STARTED_KEY = "trimupgrade_contributor_smash_started";
    private static final String TIDE_ACTIVE_TICKS_KEY = "trimupgrade_tide_active_ticks";
    private static final String TIDE_NEXT_PULSE_TICKS_KEY = "trimupgrade_tide_next_pulse_ticks";
    private static final String TIDE_PULSE_TICKS_KEY = "trimupgrade_tide_pulse_ticks";
    private static final String TIDE_PULSE_ID_KEY = "trimupgrade_tide_pulse_id";
    private static final String TIDE_TARGET_HIT_KEY_PREFIX = "trimupgrade_tide_hit_";
    private static final int TIDE_SKILL_DURATION_TICKS = 120;
    private static final int TIDE_SKILL_PULSE_INTERVAL_TICKS = 20;
    private static final int TIDE_SKILL_PULSE_DURATION_TICKS = 12;




    // Resource key for the custom Raiser trim pattern.
    private static final net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> RAISER_KEY =
            net.minecraft.resources.ResourceKey.create(
                    net.minecraft.core.registries.Registries.TRIM_PATTERN,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "raiser")
            );



    @SubscribeEvent
    // Critical hit event entry point.
    public static void onCriticalHit(CriticalHitEvent event) {
        TrimCriticalHitDispatcher.dispatch(event, PlayerTrimContextFactory.create(event.getEntity()));
    }


    @SubscribeEvent
    // Spire black-hole tick: attraction, periodic damage, final explosion, particles.
    public static void onBlackHoleTick(TickEvent.LevelTickEvent event) {

        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        ServerLevel level = (ServerLevel) event.level;

        if (level.players().isEmpty()) return;

        IntOpenHashSet processed = new IntOpenHashSet();
        for (ServerPlayer player : level.players()) {

            AABB nearby = player.getBoundingBox().inflate(96.0, 48.0, 96.0);
            level.getEntitiesOfClass(net.minecraft.world.entity.Display.ItemDisplay.class, nearby,
                    e -> e.getTags().contains("SpireBlackHole")).forEach(blackHole -> {
                if (!processed.add(blackHole.getId())) return;

            net.minecraft.nbt.CompoundTag nbt = blackHole.getPersistentData();

            int age = nbt.getInt("BHAge");
            int lvl = nbt.getInt("BHLvl");
            LivingEntity owner = null;
            if (nbt.hasUUID("BHOwner")) {
                Entity ownerEntity = level.getEntity(nbt.getUUID("BHOwner"));
                if (ownerEntity instanceof LivingEntity livingOwner) {
                    owner = livingOwner;
                }
            }
            final LivingEntity finalOwner = owner;
            final int safeLvl = Math.max(0, Math.min(4, lvl));

            Vec3 center = blackHole.position();
            double radius = 8.0;
            double pullRadius = 9.0;
            ensureSpireBlackHoleLight(level, blackHole);


            age++;
            nbt.putInt("BHAge", age);


            if (age >= 240) {
                if (finalOwner != null) {
                    float base = (finalOwner.getMaxHealth() + finalOwner.getArmorValue());
                    float percent = 0.35f + (safeLvl * 0.10f);
                    float damage = base * percent;

                    AABB blastArea = new AABB(center.x, center.y, center.z, center.x, center.y, center.z).inflate(radius);
                    level.getEntitiesOfClass(LivingEntity.class, blastArea, e -> e != finalOwner && e.isAlive()).forEach(target -> {
                        target.hurt(level.damageSources().fellOutOfWorld(), damage);
                    });
                }

                level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0f, 0.9f);
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 1, 0, 0, 0, 0);
                clearSpireBlackHoleLight(level, blackHole);
                blackHole.discard();
                return;
            }

            AABB searchArea = new AABB(center.x, center.y, center.z, center.x, center.y, center.z).inflate(pullRadius);
            int finalAge = age;
            level.getEntitiesOfClass(LivingEntity.class, searchArea, e -> e != finalOwner && e.isAlive()).forEach(target -> {
                Vec3 vec = center.subtract(target.position());
                double dist = vec.length();
                if (dist < 1.0e-4) return;


                // Iron's-style falloff (high-order near-center pull) with mod-specific tuning.
                double normalized = Math.max(0.0D, 1.0D - (dist / pullRadius));
                double strength = Math.pow(normalized, 3.6D) * 0.40D;

                // Knockback resistance weakens pull, but never grants full immunity.
                double kbResist = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                double resistanceFactor = Math.max(0.28D, Math.min(1.0D, 1.0D - kbResist));

                // Bosses are heavier: keep noticeable pull but reduce intensity.
                double bossFactor = (target instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon
                        || target instanceof net.minecraft.world.entity.boss.wither.WitherBoss) ? 0.55D : 1.0D;

                strength *= resistanceFactor * bossFactor;
                if (finalAge > 200) strength *= 1.6D;
                if (strength <= 0.0D) return;
                Vec3 dir = vec.scale(1.0 / dist);
                target.setDeltaMovement(target.getDeltaMovement().add(dir.scale(strength)));
                target.hasImpulse = true;


                if (finalAge % 15 == 0 && finalOwner != null) {
                    float base = (finalOwner.getMaxHealth() + finalOwner.getArmorValue());
                    float percent = 0.05f + (safeLvl * 0.025f);
                    float damage = base * percent;
                    target.hurt(level.damageSources().fellOutOfWorld(), damage);
                }
            });

            if ((age & 1) == 0) {
                DustParticleOptions brightPurpleDust = new DustParticleOptions(new Vector3f(0.96F, 0.32F, 1.0F), 1.12F);
                DustParticleOptions corePurpleDust = new DustParticleOptions(new Vector3f(1.0F, 0.58F, 1.0F), 0.72F);
                int streamCount = Math.max(1, (int) Math.round((28 + safeLvl * 5) * 0.5D));
                double minSpawnRadius = radius * 0.35D;
                double maxSpawnRadius = radius * 1.08D;
                double minSpawnR3 = minSpawnRadius * minSpawnRadius * minSpawnRadius;
                double maxSpawnR3 = maxSpawnRadius * maxSpawnRadius * maxSpawnRadius;

                for (int i = 0; i < streamCount; i++) {
                    // Uniform random direction on sphere (full 3D, not a planar ring).
                    double yUnit = level.random.nextDouble() * 2.0D - 1.0D;
                    double theta = level.random.nextDouble() * Math.PI * 2.0D;
                    double radial = Math.sqrt(Math.max(0.0D, 1.0D - yUnit * yUnit));
                    double dirX = radial * Math.cos(theta);
                    double dirY = yUnit;
                    double dirZ = radial * Math.sin(theta);

                    // Uniform random position within a spherical shell volume.
                    double spawnR = Math.cbrt(minSpawnR3 + level.random.nextDouble() * (maxSpawnR3 - minSpawnR3));
                    double px = center.x + dirX * spawnR;
                    double py = center.y + dirY * spawnR;
                    double pz = center.z + dirZ * spawnR;

                    // Each particle directly flies toward the center.
                    Vec3 inward = center.subtract(px, py, pz).normalize();
                    double inwardSpeed = 0.28D + level.random.nextDouble() * 0.16D + (safeLvl * 0.015D);
                    double vx = inward.x * inwardSpeed;
                    double vy = inward.y * inwardSpeed;
                    double vz = inward.z * inwardSpeed;

                    level.sendParticles(brightPurpleDust, px, py, pz, 0, vx, vy, vz, 1.0D);
                }

                // Add a subtle bright core flicker so the inflow visually converges.
                for (int i = 0; i < 4; i++) {
                    double cx = center.x + (level.random.nextDouble() - 0.5D) * 0.18D;
                    double cy = center.y + (level.random.nextDouble() - 0.5D) * 0.18D;
                    double cz = center.z + (level.random.nextDouble() - 0.5D) * 0.18D;
                    level.sendParticles(corePurpleDust, cx, cy, cz, 0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
            }
            });
        }
    }

    private static void ensureSpireBlackHoleLight(ServerLevel level, net.minecraft.world.entity.Display.ItemDisplay blackHole) {
        net.minecraft.nbt.CompoundTag nbt = blackHole.getPersistentData();
        BlockPos currentPos = BlockPos.containing(blackHole.position());

        if (nbt.contains(SPIRE_BH_LIGHT_X_KEY) && nbt.contains(SPIRE_BH_LIGHT_Y_KEY) && nbt.contains(SPIRE_BH_LIGHT_Z_KEY)) {
            BlockPos previousPos = new BlockPos(
                    nbt.getInt(SPIRE_BH_LIGHT_X_KEY),
                    nbt.getInt(SPIRE_BH_LIGHT_Y_KEY),
                    nbt.getInt(SPIRE_BH_LIGHT_Z_KEY)
            );
            if (!previousPos.equals(currentPos) && level.getBlockState(previousPos).is(Blocks.LIGHT)) {
                level.removeBlock(previousPos, false);
            }
        }

        if (!level.getBlockState(currentPos).isAir() && !level.getBlockState(currentPos).is(Blocks.LIGHT)) {
            return;
        }

        level.setBlock(currentPos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
        nbt.putInt(SPIRE_BH_LIGHT_X_KEY, currentPos.getX());
        nbt.putInt(SPIRE_BH_LIGHT_Y_KEY, currentPos.getY());
        nbt.putInt(SPIRE_BH_LIGHT_Z_KEY, currentPos.getZ());
    }

    private static void clearSpireBlackHoleLight(ServerLevel level, net.minecraft.world.entity.Display.ItemDisplay blackHole) {
        net.minecraft.nbt.CompoundTag nbt = blackHole.getPersistentData();
        if (nbt.contains(SPIRE_BH_LIGHT_X_KEY) && nbt.contains(SPIRE_BH_LIGHT_Y_KEY) && nbt.contains(SPIRE_BH_LIGHT_Z_KEY)) {
            BlockPos lightPos = new BlockPos(
                    nbt.getInt(SPIRE_BH_LIGHT_X_KEY),
                    nbt.getInt(SPIRE_BH_LIGHT_Y_KEY),
                    nbt.getInt(SPIRE_BH_LIGHT_Z_KEY)
            );
            if (level.getBlockState(lightPos).is(Blocks.LIGHT)) {
                level.removeBlock(lightPos, false);
            }
        }

        BlockPos currentPos = BlockPos.containing(blackHole.position());
        if (level.getBlockState(currentPos).is(Blocks.LIGHT)) {
            level.removeBlock(currentPos, false);
        }

        nbt.remove(SPIRE_BH_LIGHT_X_KEY);
        nbt.remove(SPIRE_BH_LIGHT_Y_KEY);
        nbt.remove(SPIRE_BH_LIGHT_Z_KEY);
    }



    @SubscribeEvent
    // Potion-effect applicability event.
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        TrimEffectApplicableDispatcher.dispatch(event, PlayerTrimContextFactory.create(player));
    }


    @SubscribeEvent
    // Silence set extra behavior on hurt.
    public static void onSilenceEffectHurt(LivingHurtEvent event) {
        SilenceSet.COMBAT.onEffectHurt(event);
    }


    // Returns true if all armor pieces share the given trim pattern.
    private static boolean isFullSet(Player player, ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> pattern) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
            var trim = net.minecraft.world.item.armortrim.ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty() || !trim.get().pattern().is(pattern)) {
                return false;
            }
        }
        return true;
    }



    // Contributor combo definition: WILD/WARD/COAST/TIDE by slot.
    private static boolean isContributorSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) return false;
        return isTrim(player, head, TrimPatterns.WILD)
                && isTrim(player, chest, TrimPatterns.WARD)
                && isTrim(player, legs, TrimPatterns.COAST)
                && isTrim(player, feet, TrimPatterns.TIDE);
    }


    @SubscribeEvent
    // Living target-change event entry point.
    public static void onTargetChange(LivingChangeTargetEvent event) {
        TrimTargetChangeDispatcher.dispatch(event);
    }



    @SubscribeEvent
    // Living hurt event entry point.
    public static void onLivingHurt(LivingHurtEvent event) {
        TrimLivingHurtDispatcher.dispatch(event);
    }


    @SubscribeEvent
    // Spire full-set fall handling.
    public static void onSpireFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        SpireSet.COMBAT.onFall(event, player, isFullSet(player, TrimPatterns.SPIRE));
    }


    @SubscribeEvent
    // Ward sneak tick on client side.
    public static void onWardSneakTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        TrimTickDispatcher.onWardSneakClientTick(event, getTrimCount(player, TrimPatterns.WARD) == 4);
    }

    @SubscribeEvent
    // Sentry remote-damage forwarding.
    public static void onSentryRemoteDamage(LivingHurtEvent event) {
        TrimCombatDispatcher.applySentryRemoteDamage(event);
    }


    @SubscribeEvent
    // Living attack event entry point.
    public static void onLivingAttack(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        TrimLivingAttackDispatcher.dispatch(event);
    }



    @SubscribeEvent
    // Recompute attributes when equipment changes.
    public static void onEquipChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            refreshAttributes(player);
        }
    }

    @SubscribeEvent
    // Recompute attributes when a player logs in.
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        refreshAttributes(player);
    }

    @SubscribeEvent
    // Recompute attributes after respawn.
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        refreshAttributes(player);
    }

    @SubscribeEvent
    // Recompute attributes after changing dimension.
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        refreshAttributes(player);
    }

    @SubscribeEvent
    // Recompute attributes for cloned player entities.
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        refreshAttributes(player);
    }



    @SubscribeEvent
    // Living tick: passive effects, status cleanup, and per-tick set logic.
	public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
            if (!event.getEntity().level().isClientSide && event.getEntity().level() instanceof ServerLevel serverLevel) {
                HighlightUtils.tickRestoreIfExpired(event.getEntity(), serverLevel);
                MarkUtils.tickCleanup(event.getEntity(), serverLevel);
            }

            if (event.getEntity() instanceof Player player) {
                if (getTrimCount(player, RAISER_KEY) >= 4 && player.getFeetBlockState().is(Blocks.POWDER_SNOW)) {

                    player.setIsInPowderSnow(false);
                }
            }

	        if (!event.getEntity().level().isClientSide && !(event.getEntity() instanceof Player)) {
	            var data = event.getEntity().getPersistentData();
	            if (data.contains(com.xiyue.trimmod.common.trim.set.spire.SpireSetConstants.MAJESTY_SLOW_UNTIL_KEY)
                        && event.getEntity().level().getGameTime() > data.getLong(com.xiyue.trimmod.common.trim.set.spire.SpireSetConstants.MAJESTY_SLOW_UNTIL_KEY)) {
	                AttributeInstance speed = event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED);
	                if (speed != null) speed.removeModifier(com.xiyue.trimmod.common.trim.set.spire.SpireSetConstants.MAJESTY_SLOW_UUID);
	                data.remove(com.xiyue.trimmod.common.trim.set.spire.SpireSetConstants.MAJESTY_SLOW_UNTIL_KEY);
	            }
	        }

	        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (isContributorSet(player) && player.isShiftKeyDown()) {
	                applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, CONTRIBUTOR_CROUCH_KB_UUID, "Contributor Crouch KB", 1.0D, AttributeModifier.Operation.ADDITION);
	                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, false, true));
                    int minLvl = Math.max(0, Math.min(4, getMinUpgradeLevel(player)));
                    int intervalHalfTicks = Math.max(1, 120 - (15 * minLvl));
                    var data = player.getPersistentData();
                    int regenHalfTicks = data.getInt(PersistentDataKeys.CONTRIBUTOR_REGEN_HALF_TICKS) + 2;
                    if (player.getHealth() < player.getMaxHealth() && regenHalfTicks >= intervalHalfTicks) {
                        player.heal(1.0F);
                        regenHalfTicks -= intervalHalfTicks;
                    }
                    data.putInt(PersistentDataKeys.CONTRIBUTOR_REGEN_HALF_TICKS, regenHalfTicks);
	            } else {
	                removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, CONTRIBUTOR_CROUCH_KB_UUID);
                    player.getPersistentData().remove(PersistentDataKeys.CONTRIBUTOR_REGEN_HALF_TICKS);
	            }


	            if (getTrimCount(player, TrimPatterns.SNOUT) >= 4 && player.isOnFire()) {
	                player.clearFire();
	            }

            int sentryCount = getTrimCount(player, TrimPatterns.SENTRY);
            TrimTickDispatcher.handleSentryFloatingEntity(player, sentryCount, getMinUpgradeLevel(player));

            if (getTrimCount(player, TrimPatterns.WAYFINDER) >= 4) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
            }

            TrimTickDispatcher.applyPassiveEffects(player);

            if (getTrimCount(player, TrimPatterns.COAST) >= 4) {
                if (player.isInWaterOrBubble()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 220, 0, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 0, false, false, true));
                }
            }

            if (player.tickCount % 20 == 0) {
                TrimTickDispatcher.applyHostPassive(player, getTrimCount(player, TrimPatterns.HOST) >= 4, getMinUpgradeLevel(player));
                if (player.tickCount % 100 == 0) {
                    int shaperCount = getTrimCount(player, TrimPatterns.SHAPER);
                    if (shaperCount >= 4) {
                        int lvl = getMinUpgradeLevel(player);
                        int repair = 1 + lvl;
                        player.getArmorSlots().forEach(s -> {
                            if (isTrim(player, s, TrimPatterns.SHAPER) && s.isDamaged()) {
                                s.setDamageValue(Math.max(0, s.getDamageValue() - repair));
                            }
                        });
                    }
                }
                refreshAttributes(player);
            }


            int coastCount = getTrimCount(player, TrimPatterns.COAST);
            if (coastCount >= 4) {
                int minLvl = getMinUpgradeLevel(player);
                float totalBoost = (0.05f + 0.025f * minLvl) * 4;
                int interval = (int)(80f / (1.0f + totalBoost));

                if (player.getHealth() < player.getMaxHealth()) {
                    if (player.tickCount % interval == 0) {
                        player.heal(1.0f);

                        if (player.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.BUBBLE,
                                    player.getX(), player.getY() + 1.0, player.getZ(),
                                    8, 0.45, 0.65, 0.45, 0.02);
                        }
                    }
                }
            }


            int raiserCount = getTrimCount(player, RAISER_KEY);
            TrimTickDispatcher.applyRaiserPassive(player, raiserCount >= 4, getMinUpgradeLevel(player));


            int wallTimer = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER);
            if (wallTimer > 0) {
                int lvl = getMinUpgradeLevel(player);

                double kbRes = 0.3 + (lvl * 0.175);
                applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, SHAPER_KNOCKBACK_RES_UUID, "Iron Wall KB", kbRes, AttributeModifier.Operation.ADDITION);
                applyModifier(player, Attributes.MOVEMENT_SPEED, SHAPER_SLOW_UUID, "Iron Wall Slow", -0.2, AttributeModifier.Operation.MULTIPLY_BASE);

                if (!player.level().isClientSide) {
                    ServerLevel serverLevel = (ServerLevel) player.level();

                    for (int i = 0; i < 4; i++) {
                        double angle = player.getRandom().nextDouble() * 2 * Math.PI;

                        double r = 0.4 + player.getRandom().nextDouble() * 0.2;
                        double px = player.getX() + Math.cos(angle) * r;
                        double pz = player.getZ() + Math.sin(angle) * r;

                        double py = player.getY() + player.getRandom().nextDouble() * 2.0;

                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                                px, py, pz, 1, 0, 0, 0, 0);
                    }
                }

                int nextWallTimer = wallTimer - 1;
                player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_TIMER, nextWallTimer);
                if (player instanceof ServerPlayer serverPlayer) {
                    int maxWallTimer = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_MAX_TIMER);
                    ModMessages.sendToPlayer(new PacketSyncShaperSkillTime(nextWallTimer, maxWallTimer), serverPlayer);
                }
            } else {

                removeModifier(player, Attributes.MOVEMENT_SPEED, SHAPER_SLOW_UUID);
                if (player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_MAX_TIMER) > 0) {
                    player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_MAX_TIMER, 0);
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModMessages.sendToPlayer(new PacketSyncShaperSkillTime(0, 0), serverPlayer);
                    }
                }
            }
        }

        if (event.getEntity() instanceof Piglin piglin && piglin.getTarget() instanceof Player player) {
	            if (getTrimCount(player, TrimPatterns.SNOUT) > 0) {
	                piglin.setTarget(null);
	            }
	        }

        if (event.getEntity() instanceof IronGolem golem && golem.getTarget() instanceof Player player) {
	            if (isFullSet(player, TrimPatterns.HOST)) {
	                golem.setTarget(null);
	            }
	        }
	    }


    // Utility: remove modifier by UUID if present.
    private static void removeModifier(Player player, Attribute attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }



    // Rebuild and apply all trim-based attribute modifiers.
    private static void refreshAttributes(Player player) {
        if (player.level().isClientSide) return;

        WildAttributes.Totals wildTotals = new WildAttributes.Totals();
        double wildArmor = 0, wildToughness = 0;
        SnoutAttributes.Totals snoutTotals = new SnoutAttributes.Totals();
        double snoutArmor = 0, snoutKnockback = 0;
        WayfinderAttributes.Totals wayfinderTotals = new WayfinderAttributes.Totals();
        double duneSpeed = 0, duneArmorReduc = 0;
        HostAttributes.Totals hostTotals = new HostAttributes.Totals();
        double hostHealth = 0, hostKb = 0;
        CoastAttributes.Totals coastTotals = new CoastAttributes.Totals();
        SentryAttributes.Totals sentryTotals = new SentryAttributes.Totals();
        double sentrySpeed = 0, sentryToughness = 0;
        ShaperAttributes.Totals shaperTotals = new ShaperAttributes.Totals();
        RaiserAttributes.Totals raiserTotals = new RaiserAttributes.Totals();
        double raiserArmor = 0, raiserAttackSpeed = 0;
        WardAttributes.Totals wardTotals = new WardAttributes.Totals();
        double wardArmor = 0, wardKnockback = 0;
        SilenceAttributes.Totals silenceTotals = new SilenceAttributes.Totals();
        RibAttributes.Totals ribTotals = new RibAttributes.Totals();
        EyeAttributes.Totals eyeTotals = new EyeAttributes.Totals();
        com.xiyue.trimmod.common.trim.set.spire.SpireAttributes.Totals spireTotals = new com.xiyue.trimmod.common.trim.set.spire.SpireAttributes.Totals();
        ContributorAttributes.Totals contributorTotals = new ContributorAttributes.Totals();

        int coastCount = 0;
        int duneCount = 0;
        int minDuneLvl = 99;
        int sentryCount = 0;
        int minSentryLvl = 99;
        int tideCount = 0;
        int minTideLvl = 99;
        int shaperCount = 0;
        int minShaperLvl = 99;
        int raiserCount = 0;
        int minRaiserLvl = 99;
        int wardCount = 0;
        int minWardLvl = 99;
        int silenceCount = 0;
        int minSilenceLvl = 99;
        int spireCount = 0;
        int minSpireLvl = 99;

        boolean isDaytime = player.level().isDay();
        boolean contributorSet = isContributorSet(player);
        DuneAttributes.Totals duneTotals = new DuneAttributes.Totals();
        TideAttributes.Totals tideTotals = new TideAttributes.Totals();
        SwampAttributes.Totals swampTotals = new SwampAttributes.Totals();
        VexAttributes.Totals vexTotals = new VexAttributes.Totals();

	        for (ItemStack stack : player.getArmorSlots()) {
	            if (stack.isEmpty()) continue;
	            int lvl = getUpgradeLevel(stack);

            if (contributorSet && (
	                    isTrim(player, stack, TrimPatterns.WILD)
	                            || isTrim(player, stack, TrimPatterns.WARD)
	                            || isTrim(player, stack, TrimPatterns.COAST)
	                            || isTrim(player, stack, TrimPatterns.TIDE)
	            )) {

	                ContributorSet.ATTRIBUTES.collect(lvl, contributorTotals);
	                continue;
	            }

            if (isTrim(player, stack, TrimPatterns.TIDE)) {
                tideCount++;
                minTideLvl = Math.min(minTideLvl, lvl);
                TideSet.ATTRIBUTES.collect(lvl, tideTotals);
            } else if (isTrim(player, stack, TrimPatterns.WILD)) {

                wildToughness += (0.2 + 0.15 * lvl);
                WildSet.ATTRIBUTES.collect(lvl, wildTotals);
            } else if (isTrim(player, stack, TrimPatterns.SNOUT)) {

                snoutKnockback += (0.03 + 0.02 * lvl);
                SnoutSet.ATTRIBUTES.collect(lvl, snoutTotals);
            } else if (isTrim(player, stack, TrimPatterns.WAYFINDER)) {

                WayfinderSet.ATTRIBUTES.collect(lvl, wayfinderTotals);
            } else if (isTrim(player, stack, TrimPatterns.VEX)) {
                VexSet.ATTRIBUTES.collect(lvl, vexTotals);
            } else if (isTrim(player, stack, TrimPatterns.DUNE)) {

                duneCount++;
                minDuneLvl = Math.min(minDuneLvl, lvl);
                DuneSet.ATTRIBUTES.collect(lvl, duneTotals);
            } else if (isTrim(player, stack, TrimPatterns.HOST)) {

                hostKb += (0.05 + 0.03 * lvl);
                HostSet.ATTRIBUTES.collect(lvl, hostTotals);
            } else if (isTrim(player, stack, TrimPatterns.COAST)) {
                coastCount++;
                CoastSet.ATTRIBUTES.collect(lvl, coastTotals);
            } else if (isTrim(player, stack, TrimPatterns.SENTRY)) {
                sentryCount++;
                minSentryLvl = Math.min(minSentryLvl, lvl);
                sentrySpeed += (0.03 + lvl * 0.02);
                sentryToughness += (0.15 + lvl * 0.0875);
                SentrySet.ATTRIBUTES.collect(lvl, sentryTotals);
            } else if (isTrim(player, stack, TrimPatterns.SHAPER)) {
                shaperCount++;
                minShaperLvl = Math.min(minShaperLvl, lvl);
                ShaperSet.ATTRIBUTES.collect(lvl, shaperTotals);
            } else if (isTrim(player, stack, RAISER)) {
                raiserCount++;
                raiserArmor += (1.0 + 0.375 * lvl);
                raiserAttackSpeed += (0.04 + 0.01 * lvl);
                RaiserSet.ATTRIBUTES.collect(lvl, raiserTotals);
            } else if (isTrim(player, stack, TrimPatterns.WARD)) {
                wardCount++;
                wardArmor += (1.0 + 0.4375 * lvl);
                wardKnockback += (0.05 + 0.03 * lvl);
                WardSet.ATTRIBUTES.collect(lvl, wardTotals);
            } else if (isTrim(player, stack, TrimPatterns.SILENCE)) {
                silenceCount++;
                SilenceSet.ATTRIBUTES.collect(lvl, silenceTotals);
            } else if (isTrim(player, stack, TrimPatterns.RIB)) {
                RibSet.ATTRIBUTES.collect(lvl, ribTotals);
            } else if (isTrim(player, stack,TrimPatterns.EYE)) {
                EyeSet.ATTRIBUTES.collect(lvl, eyeTotals);
            } else if (isTrim(player, stack, TrimPatterns.SPIRE)) {
                spireCount++;
                SpireSet.ATTRIBUTES.collect(lvl, spireTotals);
            }

            if (isTrim(player, stack, TrimPatterns.TIDE)) {
                SwampSet.ATTRIBUTES.collectTide(lvl, swampTotals);
            } else if (isTrim(player, stack, TrimPatterns.WILD)) {
                SwampSet.ATTRIBUTES.collectWild(lvl, swampTotals);
            }
        }




        double duneAttackSpeed = 0;
        if (duneCount >= 4) {

            duneAttackSpeed = 0.10 + (minDuneLvl * 0.075);
        }



        boolean swampActive = swampTotals.tideCount() >= 2 && swampTotals.wildCount() >= 2;

        if (swampActive) {
            // Swamp set overrides base Tide/Wild attributes.
            applyModifier(player, Attributes.MAX_HEALTH, TIDE_HP_UUID, "Tide HP", 0.0D, AttributeModifier.Operation.ADDITION);
            applyModifier(player, Attributes.MOVEMENT_SPEED, TIDE_SP_UUID, "Tide Speed", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
            applyModifier(player, Attributes.ATTACK_DAMAGE, TIDE_DAMAGE_UUID, "Tide Damage", 0.0D, AttributeModifier.Operation.ADDITION);
        } else {
            TrimAttributeDispatcher.applyTide(player, tideTotals, isDaytime,
                    (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));
        }

        // Always clear Swamp modifiers first to avoid stale values after deactivation.
        applyModifier(player, Attributes.MAX_HEALTH, SWAMP_HP_UUID, "Swamp HP", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.MOVEMENT_SPEED, SWAMP_SPEED_UUID, "Swamp Speed", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
        applyModifier(player, Attributes.ARMOR, SWAMP_ARMOR_UUID, "Swamp Armor", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.ARMOR_TOUGHNESS, SWAMP_TOUGHNESS_UUID, "Swamp Toughness", 0.0D, AttributeModifier.Operation.ADDITION);
        if (swampActive) {
            SwampSet.ATTRIBUTES.apply(player, swampTotals,
                    (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));
        }

        applyModifier(player, Attributes.ARMOR, WILD_ARMOR_UUID, "Wild Armor", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.ARMOR_TOUGHNESS, WILD_TOUGHNESS_UUID, "Wild Toughness", 0.0D, AttributeModifier.Operation.ADDITION);
        if (!swampActive) {
            TrimAttributeDispatcher.applyWild(player, wildTotals,
                    (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));
        }

        applyModifier(player, Attributes.ARMOR, SNOUT_ARMOR_UUID, "Snout Armor", snoutArmor, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, SNOUT_KNOCKBACK_UUID, "Snout Knockback", snoutKnockback, AttributeModifier.Operation.ADDITION);

        TrimAttributeDispatcher.applyWayfinder(player, wayfinderTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyVex(player, vexTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyDune(player, duneTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        applyModifier(player, Attributes.MAX_HEALTH, HOST_HEALTH_UUID, "Host HP", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, HOST_KB_UUID, "Host KB", 0.0D, AttributeModifier.Operation.ADDITION);
        TrimAttributeDispatcher.applyHost(player, hostTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyCoast(player, coastTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        applyModifier(player, Attributes.MOVEMENT_SPEED, SENTRY_SPEED_UUID, "Sentry Speed", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
        applyModifier(player, Attributes.ARMOR_TOUGHNESS, SENTRY_TOUGHNESS_UUID, "Sentry Toughness", 0.0D, AttributeModifier.Operation.ADDITION);
        TrimAttributeDispatcher.applySentry(player, sentryTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyShaper(player, shaperTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        applyModifier(player, Attributes.ARMOR, RAISER_ARMOR_UUID, "Raiser Armor", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.ATTACK_SPEED, RAISER_ATTACK_SPEED_UUID, "Raiser Attack Speed", 0.0D, AttributeModifier.Operation.MULTIPLY_BASE);
        TrimAttributeDispatcher.applyRaiser(player, raiserTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        applyModifier(player, Attributes.ARMOR, WARD_ARMOR_UUID, "Ward Armor", 0.0D, AttributeModifier.Operation.ADDITION);
        applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, WARD_KNOCKBACK_UUID, "Ward Knockback", 0.0D, AttributeModifier.Operation.ADDITION);
        TrimAttributeDispatcher.applyWard(player, wardTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));
        TrimAttributeDispatcher.applyContributor(player, contributorTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applySilence(player, silenceTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyRib(player, ribTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applyEye(player, eyeTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        TrimAttributeDispatcher.applySpire(player, spireTotals,
                (attribute, uuid, name, value, operation) -> applyModifier(player, attribute, uuid, name, value, operation));

        // Clamp once after the full refresh pass to avoid transient max-health dips
        // from clear-then-apply sequences (e.g. Swamp/Tide override).
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }


    @SubscribeEvent
    // Kill event: Rib full-set reward handling.
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (isFullSet(player, TrimPatterns.RIB)) {
                RibSet.TICK.onKill(player, getMinUpgradeLevel(player));
            }
        }
    }


    // Public helper for strict full-set check.
    public static boolean isRealFullSet(Player player, ResourceKey<TrimPattern> pattern) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);

            if (trim.isEmpty() || !trim.get().pattern().is(pattern)) return false;
        }
        return true;
    }


    @SubscribeEvent
    // Server-side player tick dispatch.
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            TrimPlayerTickDispatcher.dispatch(player);
        }
    }


    // Legacy snout dash state machine.
    private static void tickSnoutDash(ServerPlayer player) {
        int dashTicks = player.getPersistentData().getInt("trimupgrade_snout_dash_ticks");
        if (dashTicks <= 0) {
            return;
        }

        double dirX = player.getPersistentData().getDouble("trimupgrade_snout_dash_dir_x");
        double dirZ = player.getPersistentData().getDouble("trimupgrade_snout_dash_dir_z");
        double speed = player.getPersistentData().getDouble("trimupgrade_snout_dash_speed");
        int dashId = player.getPersistentData().getInt("trimupgrade_snout_dash_id");
        int minLvl = player.getPersistentData().getInt("trimupgrade_snout_dash_level");
        float damage = player.getPersistentData().getFloat("trimupgrade_snout_dash_damage");
        ServerLevel level = player.serverLevel();
        Vec3 dir = new Vec3(dirX, 0.0D, dirZ);
        if (dir.lengthSqr() < 1.0E-6D) {
            dir = Vec3.directionFromRotation(0.0F, player.getYRot());
        } else {
            dir = dir.normalize();
        }
        final Vec3 finalDir = dir;

        double vertical = player.onGround() ? 0.0D : Math.max(-0.12D, player.getDeltaMovement().y);
        player.setDeltaMovement(finalDir.x * speed, vertical, finalDir.z * speed);
        player.hurtMarked = true;
        player.hasImpulse = true;
        player.setSprinting(true);
        player.fallDistance = 0.0F;

        if ((player.tickCount & 1) == 0) {
            level.sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY() + 0.15D, player.getZ(),
                    4, 0.22D, 0.05D, 0.22D, 0.01D);
            level.sendParticles(ParticleTypes.SMALL_FLAME,
                    player.getX(), player.getY() + 0.1D, player.getZ(),
                    3, 0.18D, 0.04D, 0.18D, 0.005D);
            level.sendParticles(ParticleTypes.LAVA,
                    player.getX(), player.getY() + 0.08D, player.getZ(),
                    1, 0.12D, 0.03D, 0.12D, 0.0D);
        }

        AABB hitArea = player.getBoundingBox().inflate(0.55D).move(finalDir.scale(0.65D));
        level.getEntitiesOfClass(LivingEntity.class, hitArea, entity -> entity != player && entity.isAlive()).forEach(target -> {
            String hitKey = "trimupgrade_snout_hit_" + player.getId();
            if (target.getPersistentData().getInt(hitKey) == dashId) {
                return;
            }

            target.getPersistentData().putInt(hitKey, dashId);
            target.hurt(player.damageSources().playerAttack(player), damage);
            target.setSecondsOnFire(3 + minLvl);
            target.knockback(1.1D + (minLvl * 0.1D), -finalDir.x, -finalDir.z);
            level.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1.0D, target.getZ(),
                    8, 0.25D, 0.25D, 0.25D, 0.02D);
        });

        dashTicks--;
        player.getPersistentData().putInt("trimupgrade_snout_dash_ticks", dashTicks);
        if (dashTicks <= 0) {
            clearSnoutDashState(player);
        }
    }


    // Clear legacy snout dash runtime keys.
    private static void clearSnoutDashState(ServerPlayer player) {
        player.getPersistentData().putInt("trimupgrade_snout_dash_ticks", 0);
        player.getPersistentData().putInt("trimupgrade_snout_dash_max_ticks", 0);
        player.getPersistentData().remove("trimupgrade_snout_dash_dir_x");
        player.getPersistentData().remove("trimupgrade_snout_dash_dir_z");
        player.getPersistentData().remove("trimupgrade_snout_dash_speed");
        player.getPersistentData().remove("trimupgrade_snout_dash_damage");
        player.getPersistentData().remove("trimupgrade_snout_dash_level");
    }


    // Vex soul-sacrifice ambient particle ring.
    private static void emitSoulSacrificeAura(ServerPlayer player) {
        MobEffectInstance soulEffect = player.getEffect(ModEffects.SOUL_SACRIFICE.get());
        if (soulEffect == null || player.tickCount % 3 != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        int amplifier = Math.max(0, soulEffect.getAmplifier());
        int soulCount = 4 + amplifier;
        double radius = 0.28D + amplifier * 0.03D;

        for (int i = 0; i < soulCount; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = radius * (0.55D + player.getRandom().nextDouble() * 0.85D);
            double x = player.getX() + Math.cos(angle) * distance;
            double y = player.getY() + 0.25D + player.getRandom().nextDouble() * 1.35D;
            double z = player.getZ() + Math.sin(angle) * distance;

            level.sendParticles(ParticleTypes.SOUL, x, y, z, 1, 0.0D, 0.015D, 0.0D, 0.0D);
        }

        int accentCount = 1 + amplifier / 2;
        for (int i = 0; i < accentCount; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = radius * (0.35D + player.getRandom().nextDouble() * 0.65D);
            double x = player.getX() + Math.cos(angle) * distance;
            double y = player.getY() + 0.55D + player.getRandom().nextDouble() * 0.85D;
            double z = player.getZ() + Math.sin(angle) * distance;

            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.01D, 0.02D, 0.01D, 0.0D);
        }
    }


    // Tide active-skill tick loop.
    private static void tickTideSkill(ServerPlayer player, int minLvl) {
        int activeTicks = player.getPersistentData().getInt(TIDE_ACTIVE_TICKS_KEY);
        if (activeTicks <= 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        renderTideOrbit(level, player);

        int nextPulseTicks = player.getPersistentData().getInt(TIDE_NEXT_PULSE_TICKS_KEY);
        if (nextPulseTicks > 0) {
            nextPulseTicks--;
        }
        if (nextPulseTicks <= 0) {
            int nextPulseId = player.getPersistentData().getInt(TIDE_PULSE_ID_KEY) + 1;
            player.getPersistentData().putInt(TIDE_PULSE_ID_KEY, nextPulseId);
            player.getPersistentData().putInt(TIDE_PULSE_TICKS_KEY, 1);
            nextPulseTicks = TIDE_SKILL_PULSE_INTERVAL_TICKS;

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.15f, 1.75f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.85f, 1.35f);
        }
        player.getPersistentData().putInt(TIDE_NEXT_PULSE_TICKS_KEY, nextPulseTicks);

        int pulseTicks = player.getPersistentData().getInt(TIDE_PULSE_TICKS_KEY);
        if (pulseTicks > 0) {
            renderTidePulse(level, player, minLvl, pulseTicks, player.getPersistentData().getInt(TIDE_PULSE_ID_KEY));
            pulseTicks++;
            if (pulseTicks > TIDE_SKILL_PULSE_DURATION_TICKS) {
                pulseTicks = 0;
            }
            player.getPersistentData().putInt(TIDE_PULSE_TICKS_KEY, pulseTicks);
        }

        activeTicks--;
        player.getPersistentData().putInt(TIDE_ACTIVE_TICKS_KEY, activeTicks);
        if (activeTicks <= 0) {
            clearTideSkillState(player);
        }
    }


    // Tide orbiting-star particle renderer.
    private static void renderTideOrbit(ServerLevel level, ServerPlayer player) {
        double time = level.getGameTime() * 0.112D;
        double radius = 1.05D;
        double topYOffset = 1.5D;
        double bottomYOffset = 0.12D;
        int orbitCount = 12;
        double angleStep = Math.PI * 2.0D / orbitCount;

        for (int starIndex = 0; starIndex < orbitCount; starIndex++) {
            double t = starIndex / (double) (orbitCount - 1);
            double y = player.getY() + topYOffset - (topYOffset - bottomYOffset) * t;
            double direction = starIndex % 2 == 0 ? 1.0D : -1.0D;
            double phase = angleStep * starIndex + (starIndex % 2 == 0 ? 0.0D : angleStep * 0.5D);
            double angle = phase + time * direction;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;

            level.sendParticles(ModParticles.TIDE_STAR.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            for (int tail = 1; tail <= 4; tail++) {
                double tailAngle = angle - direction * tail * 0.18D;
                double tx = player.getX() + Math.cos(tailAngle) * radius;
                double tz = player.getZ() + Math.sin(tailAngle) * radius;
                level.sendParticles(ModParticles.TIDE_STAR_TRAIL.get(), tx, y, tz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }


    // Tide pulse renderer plus hit dedupe, damage, and knockback.
    private static void renderTidePulse(ServerLevel level, ServerPlayer player, int minLvl, int pulseTicks, int pulseId) {
        double maxRadius = 4.5D + (minLvl * 0.35D);
        double radius = maxRadius * pulseTicks / (double) TIDE_SKILL_PULSE_DURATION_TICKS;
        float damage = (float) (4.0D + minLvl * 1.5D);
        int directionCount = 12;
        double angleStep = Math.PI * 2.0D / directionCount;
        int waterRingCount = 180;

        double y = player.getY() + 0.75D;
        for (int i = 0; i < waterRingCount; i++) {
            double angle = Math.PI * 2.0D * i / waterRingCount;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        for (int starIndex = 0; starIndex < directionCount; starIndex++) {
            double angle = angleStep * starIndex;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ModParticles.NIGHT_STAR.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            for (int tail = 1; tail <= 2; tail++) {
                double tailRadius = Math.max(0.0D, radius - tail * 0.34D);
                double tx = player.getX() + Math.cos(angle) * tailRadius;
                double tz = player.getZ() + Math.sin(angle) * tailRadius;
                level.sendParticles(ModParticles.TIDE_STAR_TRAIL.get(), tx, y, tz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        AABB area = player.getBoundingBox().inflate(maxRadius + 1.0D, 2.5D, maxRadius + 1.0D);
        String hitKey = TIDE_TARGET_HIT_KEY_PREFIX + player.getStringUUID();
        level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != player && entity.isAlive()).forEach(target -> {
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > radius || Math.abs(target.getY() - player.getY()) > 2.5D) {
                return;
            }

            if (target.getPersistentData().getInt(hitKey) == pulseId) {
                return;
            }

            target.getPersistentData().putInt(hitKey, pulseId);
            target.hurt(player.damageSources().magic(), damage);

            Vec3 direction;
            if (horizontalDistance > 1.0E-4D) {
                direction = new Vec3(dx / horizontalDistance, 0.0D, dz / horizontalDistance);
            } else {
                direction = player.getLookAngle().normalize();
            }

            double pushScale = (0.65D + (minLvl * 0.08D)) * 1.2D;
            target.setDeltaMovement(target.getDeltaMovement().add(direction.x * pushScale, 0.18D + minLvl * 0.03D, direction.z * pushScale));
            target.hurtMarked = true;
        });
    }


    // Clear Tide active-skill runtime keys.
    private static void clearTideSkillState(ServerPlayer player) {
        player.getPersistentData().remove(TIDE_ACTIVE_TICKS_KEY);
        player.getPersistentData().remove(TIDE_NEXT_PULSE_TICKS_KEY);
        player.getPersistentData().remove(TIDE_PULSE_TICKS_KEY);
        player.getPersistentData().remove(TIDE_PULSE_ID_KEY);
    }


    @SubscribeEvent
    // Cancel fall damage while contributor smash is active/recent.
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) return;
        var data = player.getPersistentData();
        long until = data.getLong(CONTRIBUTOR_SMASH_NOFALL_UNTIL_KEY);
        if (data.getBoolean(CONTRIBUTOR_SMASH_PENDING_KEY) || player.level().getGameTime() <= until) {
            player.fallDistance = 0;
            event.setDamageMultiplier(0.0F);
            event.setCanceled(true);
        }
    }



    // Utility: replace a transient attribute modifier by UUID.
    private static void applyModifier(Player player, Attribute attribute, UUID uuid, String name, double value, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst != null) {
            inst.removeModifier(uuid);
            if (value != 0) {
                inst.addTransientModifier(new AttributeModifier(uuid, name, value, op));
            }
        }
    }


    // Legacy snout skill trigger (instant dash + nearby hit).
    public static void handleSnoutDash(Player player) {
        if (getTrimCount(player, TrimPatterns.SNOUT) >= 4) {
            Vec3 look = player.getLookAngle();
            player.setDeltaMovement(look.x * 2.2, 0.1, look.z * 2.2);
            player.hurtMarked = true;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 4));


            player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(1.5)).forEach(target -> {
                if (target != player) {
                    target.hurt(player.damageSources().playerAttack(player), 10.0F);
                    target.knockback(1.2F, -look.x, -look.z);
                }
            });
        }
    }




    // Utility: count armor pieces matching a trim pattern.
    private static int getTrimCount(Player player, net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> pattern) {
        int count = 0;
        for (ItemStack s : player.getArmorSlots()) {
            if (isTrim(player, s, pattern)) count++;
        }
        return count;
    }



    // Utility: read TrimUpgradeLevel from item NBT.
    private static int getUpgradeLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Trim")) {
            return stack.getTag().getCompound("Trim").getInt("TrimUpgradeLevel");
        }
        return 0;
    }


    // Utility: minimum upgrade level across all armor slots; 0 if incomplete.
    private static int getMinUpgradeLevel(Player player) {
        int min = 99;
        boolean hasAny = false;
        for (ItemStack s : player.getArmorSlots()) {
            if (!s.isEmpty()) {
                int lvl = getUpgradeLevel(s);
                min = Math.min(min, lvl);
                hasAny = true;
            } else {
                return 0;
            }
        }
        return hasAny ? min : 0;
    }


    // Utility: true if one armor item matches target trim pattern.
    private static boolean isTrim(Player player, ItemStack stack, net.minecraft.resources.ResourceKey<net.minecraft.world.item.armortrim.TrimPattern> patternKey) {
        if (stack.isEmpty()) return false;
        return ArmorTrim.getTrim(player.level().registryAccess(), stack)
                .map(trim -> trim.pattern().is(patternKey))
                .orElse(false);
    }
}


