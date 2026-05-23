package com.xiyue.trimmod.common.trim.set.raiser;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class RaiserCombat {
    public boolean handleFreezeImmunity(Player player, LivingHurtEvent event, boolean hasFullSet) {
        if (!hasFullSet) {
            return false;
        }
        if (!event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) {
            return false;
        }
        event.setCanceled(true);
        player.setTicksFrozen(0);
        return true;
    }
}
