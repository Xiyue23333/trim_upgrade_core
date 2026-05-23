package com.xiyue.trimmod.common.event;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModEnchantments;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class InheritanceEnchantmentHandler {
    private static final String STORED_LEVEL_KEY = "TrimUpgradeStoredLevel";
    private static final String STORED_PATTERN_KEY = "TrimUpgradeStoredPattern";
    private static final String STORED_MATERIAL_KEY = "TrimUpgradeStoredMaterial";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if ((event.player.tickCount & 7) != 0) return; // 每 8 tick 扫一次，避免频繁写 NBT

        RegistryAccess access = event.player.level().registryAccess();
        Inventory inv = event.player.getInventory();

        for (ItemStack stack : inv.items) {
            tickStack(access, stack);
        }
        for (ItemStack stack : inv.armor) {
            tickStack(access, stack);
        }
        for (ItemStack stack : inv.offhand) {
            tickStack(access, stack);
        }
    }

    private static void tickStack(RegistryAccess access, ItemStack stack) {
        if (stack.isEmpty() || !stack.is(ItemTags.TRIMMABLE_ARMOR)) return;

        var trimOpt = ArmorTrim.getTrim(access, stack);
        if (trimOpt.isEmpty()) return;

        ArmorTrim trim = trimOpt.get();
        String patternId = trim.pattern().unwrapKey().map(k -> k.location().toString()).orElse("");
        String materialId = trim.material().unwrapKey().map(k -> k.location().toString()).orElse("");
        if (patternId.isEmpty() || materialId.isEmpty()) return;

        int currentLvl = TrimUtils.getUpgradeLevel(stack);
        int inheritLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INHERITANCE.get(), stack);

        CompoundTag tag = stack.getTag();
        int storedLvl = tag != null ? tag.getInt(STORED_LEVEL_KEY) : 0;
        String storedPattern = (tag != null && tag.contains(STORED_PATTERN_KEY)) ? tag.getString(STORED_PATTERN_KEY) : "";
        String storedMaterial = (tag != null && tag.contains(STORED_MATERIAL_KEY)) ? tag.getString(STORED_MATERIAL_KEY) : "";

        boolean hasStoredTrim = !storedPattern.isEmpty() && !storedMaterial.isEmpty();
        boolean trimChanged = hasStoredTrim && (!storedPattern.equals(patternId) || !storedMaterial.equals(materialId));

        // 初始化或同步（保证未来换纹饰时能“记住”当前等级与纹饰）
        if (!hasStoredTrim) {
            CompoundTag root = stack.getOrCreateTag();
            root.putString(STORED_PATTERN_KEY, patternId);
            root.putString(STORED_MATERIAL_KEY, materialId);
            root.putInt(STORED_LEVEL_KEY, currentLvl);
            return;
        }

        if (!trimChanged) {
            // 纹饰没变：保持 storedLvl 与当前等级一致
            if (storedLvl != currentLvl) {
                stack.getOrCreateTag().putInt(STORED_LEVEL_KEY, currentLvl);
            }
            return;
        }

        // 纹饰变化：只有存在“继承”附魔才会把等级带到新纹饰上
        CompoundTag root = stack.getOrCreateTag();
        if (inheritLevel > 0) {
            int inherited = Math.min(Math.min(storedLvl, inheritLevel), 4);
            setTrimUpgradeLevel(stack, inherited);
            root.putInt(STORED_LEVEL_KEY, inherited);
        } else {
            // 没有继承：清空历史，避免之后再附魔时“追溯继承”
            root.putInt(STORED_LEVEL_KEY, currentLvl);
        }
        root.putString(STORED_PATTERN_KEY, patternId);
        root.putString(STORED_MATERIAL_KEY, materialId);
    }

    private static void setTrimUpgradeLevel(ItemStack stack, int level) {
        CompoundTag root = stack.getOrCreateTag();
        CompoundTag trimTag = root.getCompound("Trim");
        trimTag.putInt("TrimUpgradeLevel", Math.max(0, Math.min(level, 4)));
        root.put("Trim", trimTag);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide) return;

        ItemStack result = event.getCrafting();
        if (result.isEmpty() || !result.is(ItemTags.TRIMMABLE_ARMOR)) return;

        int inheritLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.INHERITANCE.get(), result);
        if (inheritLevel <= 0) return;

        RegistryAccess access = event.getEntity().level().registryAccess();
        var outTrimOpt = ArmorTrim.getTrim(access, result);
        if (outTrimOpt.isEmpty()) return;

        int currentResultLvl = TrimUtils.getUpgradeLevel(result);

        // 寻找作为“基底”的输入盔甲：必须已带纹饰且拥有升级等级
        Container inv = event.getInventory();
        ItemStack baseArmor = ItemStack.EMPTY;
        int baseLvl = 0;
        ArmorTrim baseTrim = null;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty() || !s.is(ItemTags.TRIMMABLE_ARMOR)) continue;

            var t = ArmorTrim.getTrim(access, s);
            if (t.isEmpty()) continue;

            int lvl = TrimUtils.getUpgradeLevel(s);
            if (lvl <= 0) continue;

            baseArmor = s;
            baseLvl = lvl;
            baseTrim = t.get();
            break;
        }

        if (baseArmor.isEmpty() || baseTrim == null) return;

        ArmorTrim outTrim = outTrimOpt.get();
        boolean trimChanged = !baseTrim.pattern().equals(outTrim.pattern()) || !baseTrim.material().equals(outTrim.material());
        if (!trimChanged) return;

        // “继承”每级代表“可继承的最高纹饰等级”：
        // 结果等级 = min(原纹饰等级, 继承附魔等级)，最高不超过 4。
        int inherited = Math.min(Math.min(baseLvl, inheritLevel), 4);
        if (inherited <= 0) return;
        if (currentResultLvl == inherited) return;

        CompoundTag root = result.getOrCreateTag();
        CompoundTag trimTag = root.getCompound("Trim");
        trimTag.putInt("TrimUpgradeLevel", inherited);
        root.put("Trim", trimTag);
    }
}
