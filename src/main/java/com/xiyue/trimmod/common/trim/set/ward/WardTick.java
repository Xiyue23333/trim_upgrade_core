package com.xiyue.trimmod.common.trim.set.ward;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public final class WardTick {
    public void onSneakClientTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || !player.level().isClientSide || player.tickCount % 4 != 0) {
            return;
        }
        if (!player.isShiftKeyDown()) {
            return;
        }
        player.level().addParticle(ParticleTypes.SCULK_CHARGE_POP,
                player.getRandomX(0.6D), player.getY() + 0.1, player.getRandomZ(0.6D),
                0, 0.02, 0);
    }
}
