package com.xiyue.trimmod.common.event;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public final class VillagerTradeHandler {
    private static final int MASTER_LEVEL = 5;
    private static final float WAYFINDER_TRADE_CHANCE = 0.5F;
    private static final float SHAPER_TRADE_CHANCE = 0.65F;

    private VillagerTradeHandler() {}

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        List<VillagerTrades.ItemListing> masterTrades = event.getTrades().get(MASTER_LEVEL);
        if (masterTrades == null) {
            return;
        }

        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            masterTrades.add(chanceWrappedTrade(
                    singleItemTrade(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 28, 2, 30),
                    WAYFINDER_TRADE_CHANCE
            ));
        } else if (event.getType() == VillagerProfession.FARMER) {
            masterTrades.add(singleItemTrade(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 18, 2, 30));
        } else if (event.getType() == VillagerProfession.MASON) {
            masterTrades.add(chanceWrappedTrade(
                    singleItemTrade(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 22, 2, 30),
                    SHAPER_TRADE_CHANCE
            ));
        } else if (event.getType() == VillagerProfession.ARMORER) {
            masterTrades.add(singleItemTrade(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 20, 2, 30));
        }
    }

    private static VillagerTrades.ItemListing singleItemTrade(
            Item resultItem,
            int emeraldCost,
            int maxUses,
            int villagerXp
    ) {
        return (trader, random) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, emeraldCost),
                new ItemStack(resultItem),
                maxUses,
                villagerXp,
                0.2F
        );
    }

    private static VillagerTrades.ItemListing chanceWrappedTrade(VillagerTrades.ItemListing delegate, float chance) {
        return (trader, random) -> random.nextFloat() <= chance ? delegate.getOffer(trader, random) : null;
    }
}
