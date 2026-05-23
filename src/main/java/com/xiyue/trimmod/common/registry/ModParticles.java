package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TrimMOD.MODID);

    public static final RegistryObject<SimpleParticleType> TIDE_STAR =
            PARTICLES.register("tide_star", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> NIGHT_STAR =
            PARTICLES.register("night_star", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> TIDE_STAR_TRAIL =
            PARTICLES.register("tide_star_trail", () -> new SimpleParticleType(true));

    private ModParticles() {
    }
}
