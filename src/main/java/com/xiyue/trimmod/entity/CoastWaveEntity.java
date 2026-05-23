package com.xiyue.trimmod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class CoastWaveEntity extends Entity {
    private static final int MAX_LIFE = 48;
    private static final double SPEED = 8.0D / 49.0D;
    private static final double START_WIDTH = 1.2D;
    private static final double END_WIDTH = 9.5D;

    private int lifeTime = 0;
    private float damage = 12.0f;
    private int slowDuration = 100;
    private LivingEntity owner;
    private Vec3 direction = Vec3.ZERO;
    private final List<Integer> hitEntities = new ArrayList<>();

    public CoastWaveEntity(EntityType<? extends CoastWaveEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public CoastWaveEntity(EntityType<? extends CoastWaveEntity> type, Level level, LivingEntity owner, float damage, int slowDuration) {
        this(type, level);
        this.owner = owner;
        this.damage = damage;
        this.slowDuration = slowDuration;

        Vec3 look = owner.getLookAngle();
        Vec3 horizontalVec = new Vec3(look.x, 0.0D, look.z);
        if (horizontalVec.lengthSqr() < 1.0E-4D) {
            horizontalVec = Vec3.directionFromRotation(0, owner.getYRot());
        }
        this.direction = horizontalVec.normalize();

        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.setPos(owner.getX(), owner.getY() + 0.1D, owner.getZ());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.direction = Vec3.directionFromRotation(0, this.getYRot());
        } else if (this.direction.lengthSqr() < 1.0E-4D) {
            this.direction = Vec3.directionFromRotation(0, this.getYRot());
        }

        this.setPos(getX() + direction.x * SPEED, getY(), getZ() + direction.z * SPEED);

        if (this.level().isClientSide) {
            spawnWaveParticles();
        } else {
            damageEntitiesInWave();
        }

        if (++lifeTime > MAX_LIFE) {
            this.discard();
        }
    }

    private void damageEntitiesInWave() {
        double totalWidth = getCurrentWaveWidth();
        double halfWidth = totalWidth * 0.5D;
        double arcDepth = getArcDepth(halfWidth);
        Vec3 frontCenter = this.position().add(direction.scale(arcDepth * 0.5D));

        AABB searchBox = new AABB(frontCenter, frontCenter).inflate(halfWidth + 1.25D, 1.25D, halfWidth + arcDepth + 1.25D);
        this.level().getEntitiesOfClass(LivingEntity.class, searchBox, entity -> entity != owner && entity.isAlive()).forEach(target -> {
            if (hitEntities.contains(target.getId()) || !isInsideWave(target, halfWidth, arcDepth)) {
                return;
            }

            target.hurt(this.level().damageSources().magic(), damage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slowDuration, 1));
            target.knockback(1.3F, -direction.x, -direction.z);
            hitEntities.add(target.getId());
        });
    }

    private boolean isInsideWave(LivingEntity target, double halfWidth, double arcDepth) {
        Vec3 right = getRightVector();
        Vec3 toTarget = target.position().subtract(this.position());

        double lateral = toTarget.dot(right);
        if (Math.abs(lateral) > halfWidth + 0.8D) {
            return false;
        }

        double vertical = Math.abs((target.getY() + target.getBbHeight() * 0.5D) - (this.getY() + 0.8D));
        if (vertical > 1.5D) {
            return false;
        }

        double forward = toTarget.dot(direction);
        double lateralRatio = halfWidth <= 1.0E-4D ? 0.0D : Math.abs(lateral) / halfWidth;
        double crestForward = arcDepth * (1.0D - lateralRatio * lateralRatio);
        double rearThickness = 0.65D + (1.0D - lateralRatio) * 0.35D;
        double frontThickness = 0.55D;

        return forward >= crestForward - rearThickness && forward <= crestForward + frontThickness;
    }

    private void spawnWaveParticles() {
        Vec3 right = getRightVector();
        double totalWidth = getCurrentWaveWidth();
        double halfWidth = totalWidth * 0.5D;
        double arcDepth = getArcDepth(halfWidth);
        int segments = Math.max(10, (int) Math.ceil(totalWidth * 5.0D));

        for (int i = 0; i <= segments; i++) {
            double t = -1.0D + (2.0D * i / segments);
            double absT = Math.abs(t);
            double lateral = t * halfWidth;
            double curve = arcDepth * (1.0D - t * t);

            Vec3 crestPos = this.position().add(right.scale(lateral)).add(direction.scale(curve));
            double waterBaseY = crestPos.y + 0.02D;
            double foamY = crestPos.y + 0.26D + (1.0D - absT) * 0.22D;

            Vec3 wakePos = crestPos.subtract(direction.scale(0.30D + absT * 0.18D));
            this.level().addParticle(
                    ParticleTypes.BUBBLE_POP,
                    wakePos.x,
                    waterBaseY,
                    wakePos.z,
                    direction.x * 0.04D,
                    0.01D,
                    direction.z * 0.04D
            );

            if (random.nextFloat() < 0.8f) {
                Vec3 trailingWaterPos = crestPos.subtract(direction.scale(0.48D + absT * 0.28D));
                this.level().addParticle(
                        ParticleTypes.FALLING_WATER,
                        trailingWaterPos.x,
                        trailingWaterPos.y + 0.08D,
                        trailingWaterPos.z,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }

            if (random.nextFloat() < 0.72f) {
                Vec3 underWavePos = crestPos.subtract(direction.scale(0.18D));
                this.level().addParticle(
                        ParticleTypes.BUBBLE,
                        underWavePos.x,
                        underWavePos.y + random.nextDouble() * 0.18D,
                        underWavePos.z,
                        direction.x * 0.10D,
                        0.025D,
                        direction.z * 0.10D
                );
            }

            this.level().addParticle(
                    ParticleTypes.SPLASH,
                    crestPos.x,
                    foamY,
                    crestPos.z,
                    0.0D,
                    0.11D + (1.0D - absT) * 0.05D,
                    0.0D
            );

            if (random.nextFloat() < 0.65f) {
                Vec3 foamPos = crestPos.add(direction.scale(0.06D));
                this.level().addParticle(
                        ParticleTypes.CLOUD,
                        foamPos.x,
                        foamPos.y + 0.05D,
                        foamPos.z,
                        direction.x * 0.025D,
                        0.015D,
                        direction.z * 0.025D
                );
            }

            if (random.nextFloat() < 0.35f) {
                Vec3 whiteFoamPos = crestPos.add(direction.scale(0.08D));
                this.level().addParticle(
                        ParticleTypes.WHITE_ASH,
                        whiteFoamPos.x,
                        whiteFoamPos.y + 0.08D,
                        whiteFoamPos.z,
                        0.0D,
                        0.01D,
                        0.0D
                );
            }
        }
    }

    private double getCurrentWaveWidth() {
        double progress = Math.min(1.0D, lifeTime / (double) MAX_LIFE);
        return START_WIDTH + (END_WIDTH - START_WIDTH) * progress;
    }

    private double getArcDepth(double halfWidth) {
        return 0.45D + halfWidth * 0.55D;
    }

    private Vec3 getRightVector() {
        return new Vec3(-direction.z, 0.0D, direction.x);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.damage = nbt.getFloat("Damage");
        this.slowDuration = nbt.getInt("SlowDuration");
        if (nbt.contains("DirX")) {
            this.direction = new Vec3(nbt.getDouble("DirX"), 0.0D, nbt.getDouble("DirZ"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("Damage", this.damage);
        nbt.putInt("SlowDuration", this.slowDuration);
        nbt.putDouble("DirX", this.direction.x);
        nbt.putDouble("DirZ", this.direction.z);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
