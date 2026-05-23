package com.xiyue.trimmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    private Config() {}

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue UPGRADE_TABLE_MAX_ENERGY = BUILDER
            .comment("Max energy stored in Trim Upgrade Table.")
            .defineInRange("upgradeTable.maxEnergy", 1500, 1, 1_000_000);

    private static final ForgeConfigSpec.IntValue UPGRADE_TABLE_FUEL_ENERGY = BUILDER
            .comment("Energy gained per Magic Crystal fuel item.")
            .defineInRange("upgradeTable.fuelEnergyPerCrystal", 100, 1, 1_000_000);

    private static final ForgeConfigSpec.IntValue ENERGY_COST_0_TO_1 = BUILDER
            .comment("Energy cost to upgrade TrimUpgradeLevel from 0 to 1.")
            .defineInRange("upgradeTable.energyCost.0_to_1", 135, 0, 1_000_000);

    private static final ForgeConfigSpec.IntValue ENERGY_COST_1_TO_2 = BUILDER
            .comment("Energy cost to upgrade TrimUpgradeLevel from 1 to 2.")
            .defineInRange("upgradeTable.energyCost.1_to_2", 215, 0, 1_000_000);

    private static final ForgeConfigSpec.IntValue ENERGY_COST_2_TO_3 = BUILDER
            .comment("Energy cost to upgrade TrimUpgradeLevel from 2 to 3.")
            .defineInRange("upgradeTable.energyCost.2_to_3", 295, 0, 1_000_000);

    private static final ForgeConfigSpec.IntValue ENERGY_COST_3_TO_4 = BUILDER
            .comment("Energy cost to upgrade TrimUpgradeLevel from 3 to 4.")
            .defineInRange("upgradeTable.energyCost.3_to_4", 375, 0, 1_000_000);

    private static final ForgeConfigSpec.IntValue COOLDOWN_CONTRIBUTOR_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for the Contributor set (Smash / Hammer Drop).")
            .defineInRange("activeSkills.cooldown.contributor", 30, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_TIDE_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Tide set. Implemented as Tide Energy recharge time from 0 to 100.")
            .defineInRange("activeSkills.cooldown.tide", 25, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_SPIRE_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Spire set. Implemented as Spire Energy recharge time from 0 to 100.")
            .defineInRange("activeSkills.cooldown.spire", 60, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_SNOUT_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Snout set (Hoglin Charge).")
            .defineInRange("activeSkills.cooldown.snout", 6, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_WAYFINDER_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Wayfinder set (Foresight).")
            .defineInRange("activeSkills.cooldown.wayfinder", 45, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_VEX_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Vex set (Soul Sacrifice).")
            .defineInRange("activeSkills.cooldown.vex", 60, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_COAST_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Coast set (Overwhelming Tide / Wave).")
            .defineInRange("activeSkills.cooldown.coast", 20, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_DUNE_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Dune set (Sand Tornado).")
            .defineInRange("activeSkills.cooldown.dune", 25, 0, 3600);

    private static final ForgeConfigSpec.IntValue COOLDOWN_SHAPER_SECONDS = BUILDER
            .comment("Active skill cooldown (seconds) for Shaper set (Iron Wall).")
            .defineInRange("activeSkills.cooldown.shaper", 60, 0, 3600);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int upgradeTableMaxEnergy = 1500;
    public static int fuelEnergyPerCrystal = 100;
    public static int energyCost0To1 = 135;
    public static int energyCost1To2 = 215;
    public static int energyCost2To3 = 295;
    public static int energyCost3To4 = 375;

    public static int cooldownContributorSeconds = 30;
    public static int cooldownTideSeconds = 25;
    public static int cooldownSpireSeconds = 60;
    public static int cooldownSnoutSeconds = 6;
    public static int cooldownWayfinderSeconds = 45;
    public static int cooldownVexSeconds = 60;
    public static int cooldownCoastSeconds = 20;
    public static int cooldownDuneSeconds = 25;
    public static int cooldownShaperSeconds = 60;

    public static int cooldownTicks(int seconds) {
        if (seconds <= 0) return 0;
        long ticks = (long) seconds * 20L;
        return ticks > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ticks;
    }

    public static int energyCostForUpgradeFromLevel(int currentLevel) {
        return switch (currentLevel) {
            case 0 -> energyCost0To1;
            case 1 -> energyCost1To2;
            case 2 -> energyCost2To3;
            case 3 -> energyCost3To4;
            default -> Integer.MAX_VALUE;
        };
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        upgradeTableMaxEnergy = UPGRADE_TABLE_MAX_ENERGY.get();
        fuelEnergyPerCrystal = UPGRADE_TABLE_FUEL_ENERGY.get();
        energyCost0To1 = ENERGY_COST_0_TO_1.get();
        energyCost1To2 = ENERGY_COST_1_TO_2.get();
        energyCost2To3 = ENERGY_COST_2_TO_3.get();
        energyCost3To4 = ENERGY_COST_3_TO_4.get();

        cooldownContributorSeconds = COOLDOWN_CONTRIBUTOR_SECONDS.get();
        cooldownTideSeconds = COOLDOWN_TIDE_SECONDS.get();
        cooldownSpireSeconds = COOLDOWN_SPIRE_SECONDS.get();
        cooldownSnoutSeconds = COOLDOWN_SNOUT_SECONDS.get();
        cooldownWayfinderSeconds = COOLDOWN_WAYFINDER_SECONDS.get();
        cooldownVexSeconds = COOLDOWN_VEX_SECONDS.get();
        cooldownCoastSeconds = COOLDOWN_COAST_SECONDS.get();
        cooldownDuneSeconds = COOLDOWN_DUNE_SECONDS.get();
        cooldownShaperSeconds = COOLDOWN_SHAPER_SECONDS.get();
    }
}
