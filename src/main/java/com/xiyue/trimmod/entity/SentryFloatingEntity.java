package com.xiyue.trimmod.entity;

import com.xiyue.trimmod.common.util.HighlightUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SentryFloatingEntity extends Entity implements OwnableEntity {

    private static final EntityDataAccessor<Integer> DATA_LEVEL = SynchedEntityData.defineId(SentryFloatingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(SentryFloatingEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private LivingEntity currentTarget;
    private int fireTimer = 0;
    private int clearTargetTimer = 0;
    private final Map<UUID, Long> revengeTargetsUntil = new HashMap<>();

    public SentryFloatingEntity(EntityType<? extends SentryFloatingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LEVEL, 0);
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        return (uuid == null || this.level() == null) ? null : this.level().getPlayerByUUID(uuid);
    }

    public void setOwner(@Nullable Player player) {
        if (player != null) {
            this.entityData.set(DATA_OWNER_UUID, Optional.of(player.getUUID()));
        }
    }

    public void setSentryLevel(int level) {
        this.entityData.set(DATA_LEVEL, Mth.clamp(level, 0, 4));
    }

    public int getSentryLevel() {
        return this.entityData.get(DATA_LEVEL);
    }

    @Override
    public void tick() {
        super.tick();

        // 环境粒子效果
        if (this.level().isClientSide) {
            spawnAmbientParticles();
            return;
        }

        LivingEntity owner = this.getOwner();
        if (owner instanceof Player player && player.isAlive()) {
            updateFollowPosition(player);

            // 记录最近伤害过玩家的生物
            long now = this.level().getGameTime();
            LivingEntity lastAttacker = player.getLastHurtByMob();
            if (lastAttacker != null && lastAttacker.isAlive() && lastAttacker != player) {
                revengeTargetsUntil.put(lastAttacker.getUUID(), now + 1200L); // 60 秒后过期
            }

            // 过期清理
            if (!revengeTargetsUntil.isEmpty() && this.level() instanceof ServerLevel serverLevel) {
                Iterator<Map.Entry<UUID, Long>> it = revengeTargetsUntil.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<UUID, Long> e = it.next();
                    if (e.getValue() <= now) {
                        it.remove();
                        continue;
                    }
                    Entity ent = serverLevel.getEntity(e.getKey());
                    if (!(ent instanceof LivingEntity le) || !le.isAlive()) {
                        it.remove();
                    }
                }
            }

            // 每分钟清除一次
            if (++clearTargetTimer >= 1200) {
                clearTargetTimer = 0;
                revengeTargetsUntil.clear();
                currentTarget = null;
            }

            // AI逻辑
            if (currentTarget == null || !currentTarget.isAlive() || !canSeeTarget(currentTarget) || this.distanceToSqr(currentTarget) > 225) {
                LivingEntity best = null;

                // 优先攻击最近伤害过玩家的目标
                if (!revengeTargetsUntil.isEmpty() && this.level() instanceof ServerLevel serverLevel) {
                    double bestDist = Double.MAX_VALUE;
                    for (UUID id : revengeTargetsUntil.keySet()) {
                        Entity ent = serverLevel.getEntity(id);
                        if (!(ent instanceof LivingEntity le) || !le.isAlive() || le == player) continue;
                        double d = this.distanceToSqr(le);
                        if (d <= 225 && d < bestDist && canSeeTarget(le)) {
                            bestDist = d;
                            best = le;
                        }
                    }
                }

                // 选择附近敌对生物
                if (best == null) {
                    best = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15),
                                    e -> e instanceof Enemy && e.isAlive() && canSeeTarget(e))
                            .stream()
                            .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                            .orElse(null);
                }

                this.currentTarget = best;
            }

            if (currentTarget != null && !canSeeTarget(currentTarget)) {
                currentTarget = null;
                fireTimer = 0;
            } else if (currentTarget != null && ++fireTimer >= 15) {
                performAttack(player, currentTarget);
                fireTimer = 0;
            }
        } else if (!this.level().isClientSide && this.tickCount > 20) {
            this.discard();
        }
    }

    private boolean canSeeTarget(LivingEntity target) {
        Vec3 start = this.position().add(0, 0.2, 0);
        Vec3 end = target.getEyePosition();
        HitResult hit = this.level().clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));
        return hit.getType() == HitResult.Type.MISS;
    }

    private void updateFollowPosition(Player player) {
        float baseYaw = player.yBodyRot;
        float orbit = Mth.sin((player.tickCount + this.getId()) * 0.05F) * 20.0F;

        double angle = Math.toRadians(baseYaw + 135.0F + orbit);
        double radius = 0.85D;
        double ox = -Math.sin(angle) * radius;
        double oz = Math.cos(angle) * radius;

        double hover = Math.sin((this.tickCount + this.getId()) * 0.1D) * 0.08D;
        Vec3 desired = new Vec3(
                player.getX() + ox,
                player.getY() + player.getEyeHeight() + 0.35D + hover,
                player.getZ() + oz
        );

        Vec3 current = this.position();
        Vec3 delta = desired.subtract(current);
        double dist = delta.length();

        if (dist > 6.0D) {
            this.setPos(desired.x, desired.y, desired.z);
            return;
        }

        double followFactor = 0.35D;
        Vec3 step = delta.scale(followFactor);
        double maxStep = 0.35D;
        if (step.lengthSqr() > (maxStep * maxStep)) {
            step = step.normalize().scale(maxStep);
        }

        Vec3 next = current.add(step);
        this.setPos(next.x, next.y, next.z);
        this.setYRot(baseYaw);
    }

    // 粒子效果
    private void spawnAmbientParticles() {
        if (this.tickCount % 4 != 0) return;

        this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                this.getRandomX(0.25D), this.getRandomY(), this.getRandomZ(0.25D),
                0, 0.01, 0);
        if (this.random.nextFloat() < 0.35f) {
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getRandomX(0.20D), this.getRandomY(), this.getRandomZ(0.20D),
                    0, 0.01, 0);
        }
        if (this.random.nextFloat() < 0.06f) {
            this.level().addParticle(ParticleTypes.END_ROD,
                    this.getRandomX(0.2D), this.getRandomY(), this.getRandomZ(0.2D),
                    0, 0.02, 0);
        }
    }

    private void performAttack(Player owner, LivingEntity target) {
        int lvl = getSentryLevel();
        float damage = 3.0F + (lvl * 1.5F);

        // 将伤害归属到玩家
        target.hurt(this.level().damageSources().playerAttack(owner), damage);
        // 哨兵纹饰高亮：绿色
        if (this.level() instanceof ServerLevel serverLevel) {
            HighlightUtils.applyColoredHighlight(target, serverLevel, "tu_hl_se", ChatFormatting.DARK_GREEN, 100 + (lvl * 40));
        } else {
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100 + (lvl * 40), 0));
        }

        if (target instanceof Mob mob && mob.getTarget() != owner) {
            mob.setTarget(owner);
        }

        // 射线效果
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 startPos = this.position().add(0, 0.2, 0); // 从浮游炮中心发射
            Vec3 endPos = target.getEyePosition();
            Vec3 direction = endPos.subtract(startPos);
            double distance = direction.length();
            Vec3 step = direction.normalize().scale(0.3); // 每0.3格一个点

            for (double d = 0; d < distance; d += 0.3) {
                Vec3 current = startPos.add(direction.normalize().scale(d));
                serverLevel.sendParticles(ParticleTypes.GLOW,
                        current.x, current.y, current.z, 1, 0, 0, 0, 0.01);
            }

            // 命中粒子
            serverLevel.sendParticles(ParticleTypes.FLASH, endPos.x, endPos.y, endPos.z, 1, 0, 0, 0, 0);

            // 攻击音效
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 0.3F, 1.6F + (lvl * 0.1F));

            // 目标击中声
            this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.5F, 0.8F);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.entityData.set(DATA_OWNER_UUID, Optional.of(tag.getUUID("Owner")));
        }
        if (tag.contains("SentryLevel")) {
            setSentryLevel(tag.getInt("SentryLevel"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.entityData.get(DATA_OWNER_UUID).ifPresent(uuid -> tag.putUUID("Owner", uuid));
        tag.putInt("SentryLevel", getSentryLevel());
    }
}
