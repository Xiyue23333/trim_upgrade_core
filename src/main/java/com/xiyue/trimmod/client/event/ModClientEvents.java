package com.xiyue.trimmod.client.event;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // 从 Trim 复合标签内部读取等级
        if (stack.hasTag() && stack.getTag().contains("Trim")) {
            int level = stack.getTag().getCompound("Trim").getInt("TrimUpgradeLevel");

            if (level >= 0) {
                // 1. 显示：强化等级: Lv.X (固定金色)
                event.getToolTip().add(Component.translatable("tooltip.trimupgrade.trim_upgrade_level.prefix")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable("tooltip.trimupgrade.trim_upgrade_level.value", level)
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)));

                // 2. 实现流光效果
                // 使用系统时间计算色相 (0.0 - 1.0)
                // 5000L 控制循环速度，数值越大变色越慢
                float hue = (System.currentTimeMillis() % 5000L) / 5000f;
                int color = Color.HSBtoRGB(hue, 0.7f, 1.0f); // 饱和度0.7，亮度1.0

                MutableComponent rainbowText = Component.translatable("tooltip.trimupgrade.trim_upgrade_active")
                        .withStyle(style -> style.withColor(TextColor.fromRgb(color)).withItalic(true));

                event.getToolTip().add(rainbowText);
            }
        }
    }
}
