package com.xiyue.trimmod.network;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncTideEnergy {
    private final int energy;

    public PacketSyncTideEnergy(int energy) {
        this.energy = energy;
    }

    public PacketSyncTideEnergy(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
    }

    public static void handle(PacketSyncTideEnergy msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getPersistentData().putInt(PersistentDataKeys.TIDE_ENERGY, msg.energy);
            }
        });
        context.setPacketHandled(true);
    }
}
