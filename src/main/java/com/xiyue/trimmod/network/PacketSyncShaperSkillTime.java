package com.xiyue.trimmod.network;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncShaperSkillTime {
    private final int currentTicks;
    private final int maxTicks;

    public PacketSyncShaperSkillTime(int currentTicks, int maxTicks) {
        this.currentTicks = currentTicks;
        this.maxTicks = maxTicks;
    }

    public PacketSyncShaperSkillTime(FriendlyByteBuf buf) {
        this.currentTicks = buf.readInt();
        this.maxTicks = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(currentTicks);
        buf.writeInt(maxTicks);
    }

    public static void handle(PacketSyncShaperSkillTime msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_TIMER, msg.currentTicks);
                mc.player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_MAX_TIMER, msg.maxTicks);
            }
        });
        context.setPacketHandled(true);
    }
}
