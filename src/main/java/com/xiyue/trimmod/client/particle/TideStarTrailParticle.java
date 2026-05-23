package com.xiyue.trimmod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TideStarTrailParticle extends TextureSheetParticle {
    private static final int TOTAL_FRAMES = 4;
    private final SpriteSet sprites;

    protected TideStarTrailParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 8;
        this.gravity = 0.0F;
        this.friction = 0.9F;
        this.quadSize = 0.12F;
        this.alpha = 0.72F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.setFrame();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.alpha = 0.72F * (1.0F - (float) this.age / this.lifetime);
        this.quadSize *= 0.985F;
        this.setFrame();
    }

    private void setFrame() {
        int frame = Math.min(TOTAL_FRAMES - 1, this.age * TOTAL_FRAMES / this.lifetime);
        this.setSprite(this.sprites.get(frame, TOTAL_FRAMES - 1));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new TideStarTrailParticle(level, x, y, z, this.sprites);
        }
    }
}
