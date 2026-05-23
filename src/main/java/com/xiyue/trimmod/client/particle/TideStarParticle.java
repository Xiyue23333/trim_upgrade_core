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
public class TideStarParticle extends TextureSheetParticle {
    private static final int TOTAL_FRAMES = 16;
    private final SpriteSet sprites;

    protected TideStarParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 2;
        this.gravity = 0.0F;
        this.friction = 0.92F;
        this.quadSize = 0.18F;
        this.alpha = 0.95F;
        this.rCol = 1.0F;
        this.gCol = 0.96F;
        this.bCol = 0.7F;
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

        int fadeTicks = Math.min(3, this.lifetime);
        if (this.age >= this.lifetime - fadeTicks) {
            this.alpha = 0.95F * (1.0F - (float) (this.age - (this.lifetime - fadeTicks)) / fadeTicks);
        }
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
            return new TideStarParticle(level, x, y, z, this.sprites);
        }
    }
}
