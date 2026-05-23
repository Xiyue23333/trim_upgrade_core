package com.xiyue.trimmod.common.event;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSkill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class SwampRootTeleportBlocker {
    @SubscribeEvent
    public static void onEnderEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        if (event.getEntity() instanceof LivingEntity living && SwampSkill.isEntitySwampRooted(living)) {
            event.setCanceled(true);
        }
    }
}
