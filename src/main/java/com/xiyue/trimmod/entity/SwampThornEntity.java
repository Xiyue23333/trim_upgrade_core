package com.xiyue.trimmod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class SwampThornEntity extends Entity implements GeoEntity {
    private static final String LIFE_TAG = "Life";
    private static final String TARGET_UUID_TAG = "TargetUuid";
    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlay("start");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SwampThornEntity(EntityType<? extends SwampThornEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public void setLife(int ticks) {
        this.getPersistentData().putInt(LIFE_TAG, Math.max(1, ticks));
    }

    public int getLife() {
        int life = this.getPersistentData().getInt(LIFE_TAG);
        return life > 0 ? life : 120;
    }

    public void setBoundTarget(LivingEntity target) {
        if (target != null) {
            this.getPersistentData().putUUID(TARGET_UUID_TAG, target.getUUID());
        }
    }

    public boolean isBoundTo(LivingEntity target) {
        if (target == null || !this.getPersistentData().hasUUID(TARGET_UUID_TAG)) {
            return false;
        }
        return target.getUUID().equals(this.getPersistentData().getUUID(TARGET_UUID_TAG));
    }

    private Optional<LivingEntity> getBoundTarget() {
        if (!this.getPersistentData().hasUUID(TARGET_UUID_TAG) || !(this.level() instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        UUID uuid = this.getPersistentData().getUUID(TARGET_UUID_TAG);
        Entity entity = serverLevel.getEntity(uuid);
        if (entity instanceof LivingEntity living && living.isAlive()) {
            return Optional.of(living);
        }
        return Optional.empty();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            getBoundTarget().ifPresent(target -> {
                this.setPos(target.getX(), target.getY(), target.getZ());
                this.setYRot(target.getYRot());
            });

            int life = getLife() - 1;
            this.getPersistentData().putInt(LIFE_TAG, life);
            if (life <= 0) {
                this.discard();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(LIFE_TAG)) {
            this.getPersistentData().putInt(LIFE_TAG, tag.getInt(LIFE_TAG));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(LIFE_TAG, getLife());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0,
                state -> state.setAndContinue(OPEN_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
