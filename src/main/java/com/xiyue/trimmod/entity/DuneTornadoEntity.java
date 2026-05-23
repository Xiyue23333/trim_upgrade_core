package com.xiyue.trimmod.entity;

import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.UUID;

public class DuneTornadoEntity extends Entity {
    private static final String LIFE_TAG = "Life";
    private static final String HEIGHT_TAG = "Height";
    private static final String END_RADIUS_TAG = "EndRadius";
    private static final String MID_RADIUS_TAG = "MidRadius";
    private static final String BASE_Y_TAG = "BaseY";
    private static final String DIR_X_TAG = "DirX";
    private static final String DIR_Z_TAG = "DirZ";
    private static final String SPEED_TAG = "Speed";
    private static final String DAMAGE_TAG = "Damage";
    private static final String HIT_INTERVAL_TAG = "HitInterval";
    private static final String LEVEL_TAG = "SkillLevel";
    private static final String OWNER_TAG = "Owner";

    public DuneTornadoEntity(EntityType<? extends DuneTornadoEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public void setLife(int ticks) {
        getPersistentData().putInt(LIFE_TAG, ticks);
    }

    public int getLife() {
        int v = getPersistentData().getInt(LIFE_TAG);
        return v <= 0 ? 80 : v;
    }

    public void setHeightBlocks(float height) {
        getPersistentData().putFloat(HEIGHT_TAG, height);
    }

    public float getHeightBlocks() {
        float v = getPersistentData().getFloat(HEIGHT_TAG);
        return v <= 0.0F ? 4.6F : v;
    }

    public void setEndRadius(float r) {
        getPersistentData().putFloat(END_RADIUS_TAG, r);
    }

    public float getEndRadius() {
        float v = getPersistentData().getFloat(END_RADIUS_TAG);
        return v <= 0.0F ? 1.15F : v;
    }

    public void setMidRadius(float r) {
        getPersistentData().putFloat(MID_RADIUS_TAG, r);
    }

    public float getMidRadius() {
        float v = getPersistentData().getFloat(MID_RADIUS_TAG);
        return v <= 0.0F ? 0.42F : v;
    }

    public void setBaseY(double y) {
        getPersistentData().putDouble(BASE_Y_TAG, y);
    }

    public double getBaseY() {
        return getPersistentData().contains(BASE_Y_TAG) ? getPersistentData().getDouble(BASE_Y_TAG) : getY();
    }

    public void setTravel(Vec3 dir, double speed) {
        Vec3 flat = new Vec3(dir.x, 0.0D, dir.z);
        if (flat.lengthSqr() < 1.0E-6) {
            flat = new Vec3(0.0D, 0.0D, 1.0D);
        }
        flat = flat.normalize();
        getPersistentData().putDouble(DIR_X_TAG, flat.x);
        getPersistentData().putDouble(DIR_Z_TAG, flat.z);
        getPersistentData().putDouble(SPEED_TAG, speed);
    }

    public void setDamagePerHit(float damage) {
        getPersistentData().putFloat(DAMAGE_TAG, damage);
    }

    public float getDamagePerHit() {
        float v = getPersistentData().getFloat(DAMAGE_TAG);
        return v <= 0.0F ? 3.0F : v;
    }

    public void setHitInterval(int ticks) {
        getPersistentData().putInt(HIT_INTERVAL_TAG, ticks);
    }

    public int getHitInterval() {
        int v = getPersistentData().getInt(HIT_INTERVAL_TAG);
        return v <= 0 ? 12 : v;
    }

    public void setSkillLevel(int level) {
        getPersistentData().putInt(LEVEL_TAG, level);
    }

    public int getSkillLevel() {
        return Math.max(0, getPersistentData().getInt(LEVEL_TAG));
    }

    public void setOwnerUuid(UUID uuid) {
        if (uuid != null) {
            getPersistentData().putUUID(OWNER_TAG, uuid);
        }
    }

    public UUID getOwnerUuid() {
        return getPersistentData().hasUUID(OWNER_TAG) ? getPersistentData().getUUID(OWNER_TAG) : null;
    }

    private Vec3 getBaseDir() {
        double x = getPersistentData().getDouble(DIR_X_TAG);
        double z = getPersistentData().getDouble(DIR_Z_TAG);
        Vec3 v = new Vec3(x, 0.0D, z);
        if (v.lengthSqr() < 1.0E-6) {
            v = new Vec3(0.0D, 0.0D, 1.0D);
        }
        return v.normalize();
    }

    private double getBaseSpeed() {
        double v = getPersistentData().getDouble(SPEED_TAG);
        return v <= 0.0D ? 0.105D : v;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(LIFE_TAG)) getPersistentData().putInt(LIFE_TAG, tag.getInt(LIFE_TAG));
        if (tag.contains(HEIGHT_TAG)) getPersistentData().putFloat(HEIGHT_TAG, tag.getFloat(HEIGHT_TAG));
        if (tag.contains(END_RADIUS_TAG)) getPersistentData().putFloat(END_RADIUS_TAG, tag.getFloat(END_RADIUS_TAG));
        if (tag.contains(MID_RADIUS_TAG)) getPersistentData().putFloat(MID_RADIUS_TAG, tag.getFloat(MID_RADIUS_TAG));
        if (tag.contains(BASE_Y_TAG)) getPersistentData().putDouble(BASE_Y_TAG, tag.getDouble(BASE_Y_TAG));
        if (tag.contains(DIR_X_TAG)) getPersistentData().putDouble(DIR_X_TAG, tag.getDouble(DIR_X_TAG));
        if (tag.contains(DIR_Z_TAG)) getPersistentData().putDouble(DIR_Z_TAG, tag.getDouble(DIR_Z_TAG));
        if (tag.contains(SPEED_TAG)) getPersistentData().putDouble(SPEED_TAG, tag.getDouble(SPEED_TAG));
        if (tag.contains(DAMAGE_TAG)) getPersistentData().putFloat(DAMAGE_TAG, tag.getFloat(DAMAGE_TAG));
        if (tag.contains(HIT_INTERVAL_TAG)) getPersistentData().putInt(HIT_INTERVAL_TAG, tag.getInt(HIT_INTERVAL_TAG));
        if (tag.contains(LEVEL_TAG)) getPersistentData().putInt(LEVEL_TAG, tag.getInt(LEVEL_TAG));
        if (tag.hasUUID(OWNER_TAG)) getPersistentData().putUUID(OWNER_TAG, tag.getUUID(OWNER_TAG));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(LIFE_TAG, getLife());
        tag.putFloat(HEIGHT_TAG, getHeightBlocks());
        tag.putFloat(END_RADIUS_TAG, getEndRadius());
        tag.putFloat(MID_RADIUS_TAG, getMidRadius());
        tag.putDouble(BASE_Y_TAG, getBaseY());
        tag.putDouble(DIR_X_TAG, getPersistentData().getDouble(DIR_X_TAG));
        tag.putDouble(DIR_Z_TAG, getPersistentData().getDouble(DIR_Z_TAG));
        tag.putDouble(SPEED_TAG, getBaseSpeed());
        tag.putFloat(DAMAGE_TAG, getDamagePerHit());
        tag.putInt(HIT_INTERVAL_TAG, getHitInterval());
        tag.putInt(LEVEL_TAG, getSkillLevel());
        UUID owner = getOwnerUuid();
        if (owner != null) {
            tag.putUUID(OWNER_TAG, owner);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            int life = getLife() - 1;
            getPersistentData().putInt(LIFE_TAG, life);
            if (life <= 0) {
                discard();
                return;
            }

            advance();
            applyTornadoForcesAndDamage();

            if ((tickCount % 14) == 0) {
                level().playSound(null, BlockPos.containing(position()), SoundEvents.SAND_BREAK, SoundSource.AMBIENT, 0.22F, 0.55F);
            }
        } else {
            spawnSandParticles();
        }
    }

    private void advance() {
        Vec3 dir = getBaseDir();
        Vec3 right = new Vec3(-dir.z, 0.0D, dir.x);
        double speed = getBaseSpeed();
        double weave = Math.sin((tickCount + (getId() % 97)) * 0.16D) * 0.095D
                + Math.sin((tickCount + (getId() % 31)) * 0.29D) * 0.034D;
        double surge = Math.sin((tickCount + (getId() % 53)) * 0.14D) * 0.012D;
        double jitter = (random.nextDouble() - 0.5D) * 0.015D;
        Vec3 move = dir.scale(speed + surge).add(right.scale(weave + jitter));

        double targetBaseY = sampleGroundYAt(getX() + move.x, getZ() + move.z);
        double nextBaseY = approach(getBaseY(), targetBaseY, 0.14D, 0.22D);
        setBaseY(nextBaseY);

        double y = nextBaseY + Math.sin((tickCount + (getId() % 41)) * 0.18D) * 0.02D;
        setPos(getX() + move.x, y, getZ() + move.z);
        setDeltaMovement(move);
    }

    private void applyTornadoForcesAndDamage() {
        float height = getHeightBlocks();
        float endR = getEndRadius();
        float midR = getMidRadius();
        Vec3 moveDir = getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 ? getDeltaMovement().normalize() : getBaseDir();
        Vec3 forward = new Vec3(moveDir.x, 0.0D, moveDir.z).normalize();
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
        Vec3 tornadoMotion = getDeltaMovement();
        float maxR = endR + 3.05F;
        AABB box = new AABB(getX() - maxR, getY(), getZ() - maxR, getX() + maxR, getY() + height, getZ() + maxR);

        LivingEntity owner = resolveOwner();
        UUID ownerId = getOwnerUuid();
        int interval = getHitInterval();
        boolean canHit = interval > 0 && tickCount % interval == 0;

        for (LivingEntity le : level().getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive())) {
            if (ownerId != null && ownerId.equals(le.getUUID())) continue;
            if (owner != null && le == owner) continue;
            if (le instanceof Player p && p.isSpectator()) continue;

            double dy = le.getY() - getY();
            float u = Mth.clamp((float) (dy / (double) height), 0.0F, 1.0F);
            float rAtY = radiusAt(u, midR, endR);
            Vec3 spineOffset = getSpineOffset(u, forward, right);
            double centerX = getX() + spineOffset.x;
            double centerZ = getZ() + spineOffset.z;
            double damageRange = rAtY + 0.85D;
            double clingRange = damageRange + 0.55D;
            double suctionRange = clingRange + 1.55D;

            Vec3 toCenter = new Vec3(centerX - le.getX(), 0.0D, centerZ - le.getZ());
            double dist = Math.sqrt(toCenter.x * toCenter.x + toCenter.z * toCenter.z);
            if (dist < 1.0E-4D || dist > suctionRange) continue;

            Vec3 dir = toCenter.scale(1.0D / dist);
            Vec3 tangent = new Vec3(-dir.z, 0.0D, dir.x);
            double falloff = 1.0D - (dist / suctionRange);
            boolean cling = dist <= clingRange;
            double innerFactor = cling ? 1.28D : (0.56D + 0.52D * falloff);
            double pull = 0.105D * falloff * innerFactor;
            double spin = 0.110D * (0.48D + 0.52D * falloff) * innerFactor;
            double lift = (0.022D + 0.030D * falloff) * innerFactor * (1.0D - 0.22D * u);
            double forwardCarryScale = cling ? 1.55D : (0.40D + 0.45D * falloff);
            Vec3 carry = new Vec3(tornadoMotion.x * forwardCarryScale, 0.0D, tornadoMotion.z * forwardCarryScale);

            Vec3 add = dir.scale(pull).add(tangent.scale(spin)).add(carry).add(0.0D, lift, 0.0D);
            Vec3 vel = le.getDeltaMovement().scale(cling ? 0.72D : 0.88D).add(add);
            le.setDeltaMovement(
                    Mth.clamp(vel.x, -0.92D, 0.92D),
                    Mth.clamp(vel.y, -0.22D, 0.60D),
                    Mth.clamp(vel.z, -0.92D, 0.92D)
            );
            le.hurtMarked = true;

            if (canHit && dist <= damageRange) {
                DamageSource source = owner != null ? level().damageSources().indirectMagic(this, owner) : level().damageSources().magic();
                le.hurt(source, getDamagePerHit());
                int curseDuration = 60 + (getSkillLevel() * 20);
                int curseAmplifier = Mth.clamp(getSkillLevel(), 0, 4);
                le.addEffect(new MobEffectInstance(ModEffects.PHARAO_CURSE.get(), curseDuration, curseAmplifier));
            }
        }
    }

    private LivingEntity resolveOwner() {
        UUID ownerId = getOwnerUuid();
        if (ownerId == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        Entity entity = serverLevel.getEntity(ownerId);
        return entity instanceof LivingEntity living ? living : null;
    }

    private double sampleGroundYAt(double x, double z) {
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        int topAir = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
        return (double) (topAir - 1) + 0.02D;
    }

    private static double approach(double current, double target, double maxUp, double maxDown) {
        double d = target - current;
        if (d > 0.0D) return current + Math.min(d, maxUp);
        return current + Math.max(d, -maxDown);
    }

    private Vec3 getSpineOffset(float u, Vec3 forward, Vec3 right) {
        double bend = 0.06D + 0.58D * Math.pow(u, 1.38D);
        double sidePrimary = Math.sin((tickCount + (getId() % 71)) * 0.115D + u * 3.7D);
        double sideSecondary = Math.sin((tickCount + (getId() % 29)) * 0.205D + u * 8.4D) * 0.68D;
        double foreSway = Math.cos((tickCount + (getId() % 47)) * 0.095D + u * 4.6D) * (0.04D + 0.22D * u * u);

        return right.scale((sidePrimary + sideSecondary) * bend).add(forward.scale(foreSway));
    }

    private void spawnSandParticles() {
        float height = getHeightBlocks();
        float endR = getEndRadius();
        float midR = getMidRadius();
        float baseR = radiusAt(0.0F, midR, endR);

        float t = tickCount + (random.nextFloat() - 0.5F) * 0.12F;
        float spinSpeed = 0.42F + (float) Math.sin(tickCount * 0.07F) * 0.06F;
        float twist = 1.12F;

        float swayT = (tickCount + (getId() % 137)) * 0.07F;
        double cx = getX() + Math.sin(swayT) * 0.10D;
        double cz = getZ() + Math.cos(swayT * 0.92F) * 0.10D;

        Vec3 vel = getDeltaMovement();
        Vec3 vFlat = new Vec3(vel.x, 0.0D, vel.z);
        Vec3 leanDir = vFlat.lengthSqr() < 1.0E-6 ? new Vec3(0.0D, 0.0D, 1.0D) : vFlat.normalize();
        Vec3 leanRight = new Vec3(-leanDir.z, 0.0D, leanDir.x);

        int layers = 16;
        float layerStep = height / (layers - 1.0F);
        int pointsPerLayer = 14;

        ParticleOptions sand1 = new DustParticleOptions(new Vector3f(0.93F, 0.83F, 0.48F), 1.08F);
        ParticleOptions sand2 = new DustParticleOptions(new Vector3f(0.84F, 0.74F, 0.42F), 0.92F);
        ParticleOptions sand3 = new DustParticleOptions(new Vector3f(0.78F, 0.67F, 0.36F), 0.86F);
        ParticleOptions sandGlow = new DustParticleOptions(new Vector3f(0.99F, 0.93F, 0.60F), 1.25F);
        BlockParticleOption fallingSand = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());
        boolean emitMainLayers = (tickCount & 1) == 0;

        if (emitMainLayers) {
            for (int i = 0; i < layers; i++) {
                float yOff = i * layerStep;
                float u = i / (layers - 1.0F);
                float r = radiusAt(u, midR, endR);
                float endness = (float) Math.pow(Math.abs(u - 0.5F) * 2.0F, 0.62F);
                float density = 0.38F + 0.52F * endness;
                float baseAngle = t * spinSpeed + (yOff * twist) + (random.nextFloat() - 0.5F) * 0.06F;

                for (int j = 0; j < pointsPerLayer; j++) {
                    if (random.nextFloat() > density) continue;

                    float ang = baseAngle + (float) (j * (Math.PI * 2.0 / pointsPerLayer)) + (random.nextFloat() - 0.5F) * 0.18F;
                    float rr = r * (0.86F + random.nextFloat() * 0.38F);
                    double lean = 0.06D + 0.20D * u;
                    Vec3 spineOffset = getSpineOffset(u, leanDir, leanRight);
                    double wobbleAmp = (0.02D + 0.16D * u) * (0.55D + 0.45D * Math.sin((tickCount + (getId() % 73)) * 0.08D));
                    double wobblePhase = (tickCount * 0.13D) + (u * 5.2D) + ((getId() % 17) * 0.31D);
                    double wobbleX = (Math.sin(wobblePhase) + 0.65D * Math.sin(wobblePhase * 1.91D)) * wobbleAmp;
                    double wobbleZ = (Math.cos(wobblePhase * 0.93D) + 0.55D * Math.cos(wobblePhase * 1.47D)) * wobbleAmp;

                    double lcx = cx + spineOffset.x + leanDir.x * lean + leanRight.x * wobbleX;
                    double lcz = cz + spineOffset.z + leanDir.z * lean + leanRight.z * wobbleX;
                    lcx += wobbleZ * 0.25D;
                    lcz += wobbleZ;

                    double px = lcx + Math.cos(ang) * rr;
                    double py = getY() + yOff + (random.nextFloat() - 0.5F) * 0.05F;
                    double pz = lcz + Math.sin(ang) * rr;

                    double tx = -Math.sin(ang);
                    double tz = Math.cos(ang);
                    double speed = 0.030D + random.nextDouble() * 0.030D;
                    double vx = tx * speed;
                    double vz = tz * speed;
                    double vy = 0.016D + random.nextDouble() * 0.026D + (0.009D * (1.0D - u));

                    float pick = random.nextFloat();
                    ParticleOptions particle = pick < 0.14F ? sandGlow : (pick < 0.55F ? sand1 : (pick < 0.84F ? sand2 : sand3));
                    level().addParticle(particle, px, py, pz, vx, vy, vz);

                    if (random.nextFloat() < 0.02F) {
                        level().addParticle(fallingSand, px, py, pz, vx * 0.4D, vy * 0.15D, vz * 0.4D);
                    }
                    if (random.nextFloat() < 0.03F) {
                        float r2 = rr * (1.10F + random.nextFloat() * 0.25F);
                        double wx = lcx + Math.cos(ang) * r2;
                        double wz = lcz + Math.sin(ang) * r2;
                        level().addParticle(sand3, wx, py + 0.02D, wz, vx * 0.55D, vy * 0.75D, vz * 0.55D);
                    }
                }
            }
        }

        if ((tickCount % 6) == 0) {
            int n = 14;
            Vec3 baseSpineOffset = getSpineOffset(0.05F, leanDir, leanRight);
            for (int i = 0; i < n; i++) {
                float ang = (tickCount * 0.36F) + (float) (i * (Math.PI * 2.0 / n)) + (random.nextFloat() - 0.5F) * 0.25F;
                float rr = baseR * (0.75F + random.nextFloat() * 0.45F);
                double px = cx + baseSpineOffset.x + Math.cos(ang) * rr;
                double pz = cz + baseSpineOffset.z + Math.sin(ang) * rr;
                double ox = Math.cos(ang) * 0.012D;
                double oz = Math.sin(ang) * 0.012D;
                level().addParticle(sand2, px, getY() + 0.05D, pz, ox, 0.020D + random.nextDouble() * 0.010D, oz);
                if (random.nextFloat() < 0.05F) {
                    level().addParticle(fallingSand, px, getY() + 0.03D, pz, ox * 0.65D, 0.006D, oz * 0.65D);
                }
            }
        }

        if (random.nextFloat() < 0.22F) {
            int p = 6;
            double topY = getY() + height * 0.92D;
            Vec3 topSpineOffset = getSpineOffset(0.92F, leanDir, leanRight);
            for (int i = 0; i < p; i++) {
                float ang = (tickCount * 0.28F) + random.nextFloat() * 6.28F;
                float rr = endR * (0.45F + random.nextFloat() * 0.45F);
                double px = cx + topSpineOffset.x + Math.cos(ang) * rr;
                double pz = cz + topSpineOffset.z + Math.sin(ang) * rr;
                double vx = Math.cos(ang) * (0.010D + random.nextDouble() * 0.010D);
                double vz = Math.sin(ang) * (0.010D + random.nextDouble() * 0.010D);
                level().addParticle(sand1, px, topY + random.nextDouble() * 0.18D, pz, vx, 0.020D + random.nextDouble() * 0.020D, vz);
            }
        }

        if (random.nextFloat() < 0.18F) {
            Vec3 back = leanDir.scale(-0.30D);
            double px = getX() + back.x + (random.nextDouble() - 0.5D) * 0.25D;
            double pz = getZ() + back.z + (random.nextDouble() - 0.5D) * 0.25D;
            level().addParticle(sand2, px, getY() + 0.05D, pz, 0.0D, 0.010D, 0.0D);
        }
    }

    private static float radiusAt(float u, float midR, float endR) {
        float clamped = Mth.clamp(u, 0.0F, 1.0F);
        float flare = clamped * clamped * (3.0F - 2.0F * clamped);
        return Mth.lerp(flare, midR, endR);
    }
}
