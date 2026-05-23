package com.xiyue.trimmod.network;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncEyeCharge {
    private final int charge;

    public PacketSyncEyeCharge(int charge) {
        this.charge = charge;
    }

    public PacketSyncEyeCharge(FriendlyByteBuf buf) {
        this.charge = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(charge);
    }

    public static void handle(PacketSyncEyeCharge msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getPersistentData().putInt(PersistentDataKeys.EYE_CHARGE_COUNT, msg.charge);
            }
        });
        context.setPacketHandled(true);
    }
}
