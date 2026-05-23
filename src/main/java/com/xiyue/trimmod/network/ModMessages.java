package com.xiyue.trimmod.network;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "messages"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        INSTANCE = net;

        registerC2S(net, PacketActiveSkill.class, PacketActiveSkill::new, PacketActiveSkill::toBytes, PacketActiveSkill::handle);

        registerS2C(net, PacketSyncSilenceCharge.class, PacketSyncSilenceCharge::new, PacketSyncSilenceCharge::toBytes, PacketSyncSilenceCharge::handle);
        registerS2C(net, PacketSyncRibCharge.class, PacketSyncRibCharge::new, PacketSyncRibCharge::toBytes, PacketSyncRibCharge::handle);
        registerS2C(net, PacketSyncEyeCharge.class, PacketSyncEyeCharge::new, PacketSyncEyeCharge::toBytes, PacketSyncEyeCharge::handle);
        registerS2C(net, PacketSyncTideEnergy.class, PacketSyncTideEnergy::new, PacketSyncTideEnergy::toBytes, PacketSyncTideEnergy::handle);
        registerS2C(net, PacketSyncSpireEnergy.class, PacketSyncSpireEnergy::new, PacketSyncSpireEnergy::toBytes, PacketSyncSpireEnergy::handle);
        registerS2C(net, PacketSyncShaperEnergy.class, PacketSyncShaperEnergy::new, PacketSyncShaperEnergy::toBytes, PacketSyncShaperEnergy::handle);
        registerS2C(net, PacketSyncWayfinderEnergy.class, PacketSyncWayfinderEnergy::new, PacketSyncWayfinderEnergy::toBytes, PacketSyncWayfinderEnergy::handle);
        registerS2C(net, PacketSyncVexEnergy.class, PacketSyncVexEnergy::new, PacketSyncVexEnergy::toBytes, PacketSyncVexEnergy::handle);
        registerS2C(net, PacketSyncDuneEnergy.class, PacketSyncDuneEnergy::new, PacketSyncDuneEnergy::toBytes, PacketSyncDuneEnergy::handle);
        registerS2C(net, PacketSyncSnoutEnergy.class, PacketSyncSnoutEnergy::new, PacketSyncSnoutEnergy::toBytes, PacketSyncSnoutEnergy::handle);
        registerS2C(net, PacketSyncCoastEnergy.class, PacketSyncCoastEnergy::new, PacketSyncCoastEnergy::toBytes, PacketSyncCoastEnergy::handle);
        registerS2C(net, PacketSyncShaperSkillTime.class, PacketSyncShaperSkillTime::new, PacketSyncShaperSkillTime::toBytes, PacketSyncShaperSkillTime::handle);
    }

    private static <MSG> void registerC2S(
            SimpleChannel net,
            Class<MSG> type,
            Function<FriendlyByteBuf, MSG> decoder,
            BiConsumer<MSG, FriendlyByteBuf> encoder,
            BiConsumer<MSG, java.util.function.Supplier<NetworkEvent.Context>> handler
    ) {
        net.messageBuilder(type, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(decoder)
                .encoder(encoder)
                .consumerMainThread(handler)
                .add();
    }

    private static <MSG> void registerS2C(
            SimpleChannel net,
            Class<MSG> type,
            Function<FriendlyByteBuf, MSG> decoder,
            BiConsumer<MSG, FriendlyByteBuf> encoder,
            BiConsumer<MSG, java.util.function.Supplier<NetworkEvent.Context>> handler
    ) {
        net.messageBuilder(type, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(decoder)
                .encoder(encoder)
                .consumerMainThread(handler)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
