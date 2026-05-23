package com.xiyue.trimmod.network;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncSilenceCharge {
    private final int charges;

    public PacketSyncSilenceCharge(int charges) {
        this.charges = charges;
    }

    public PacketSyncSilenceCharge(FriendlyByteBuf buf) {
        this.charges = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.charges);
    }

    public static void handle(PacketSyncSilenceCharge msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getPersistentData().putInt(PersistentDataKeys.SILENCE_CHARGE_COUNT, msg.charges);
            }
        });
        context.setPacketHandled(true);
    }
}
