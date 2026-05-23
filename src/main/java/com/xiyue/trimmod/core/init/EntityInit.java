package com.xiyue.trimmod.core.init;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.entity.CoastWaveEntity;
import com.xiyue.trimmod.entity.DuneTornadoEntity;
import com.xiyue.trimmod.entity.SentryFloatingEntity;
import com.xiyue.trimmod.entity.SwampThornEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TrimMOD.MODID);

    public static final RegistryObject<EntityType<CoastWaveEntity>> COAST_WAVE =
            ENTITIES.register("coast_wave", () -> EntityType.Builder.<CoastWaveEntity>of(CoastWaveEntity::new, MobCategory.MISC)
                    .sized(1.5F, 1.0F)
                    .clientTrackingRange(10)
                    .build("coast_wave"));
    public static final RegistryObject<EntityType<SentryFloatingEntity>> SENTRY_FLOATING =
            ENTITIES.register("sentry_floating", () -> EntityType.Builder.<SentryFloatingEntity>of(SentryFloatingEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("sentry_floating"));
    public static final RegistryObject<EntityType<DuneTornadoEntity>> DUNE_TORNADO =
            ENTITIES.register("dune_tornado", () -> EntityType.Builder.<DuneTornadoEntity>of(DuneTornadoEntity::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("dune_tornado"));

    public static final RegistryObject<EntityType<SwampThornEntity>> SWAMP_THORN =
            ENTITIES.register("swamp_thorn", () -> EntityType.Builder.<SwampThornEntity>of(SwampThornEntity::new, MobCategory.MISC)
                    .sized(0.01F, 0.01F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("swamp_thorn"));
}
