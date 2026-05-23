package com.xiyue.trimmod.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class HighlightUtils {
    private HighlightUtils() {}

    private static final String KEY_PREV_TEAM = "trimupgrade_hl_prev_team";
    private static final String KEY_ACTIVE_TEAM = "trimupgrade_hl_active_team";
    private static final String KEY_UNTIL = "trimupgrade_hl_until";

    public static void applyColoredHighlight(LivingEntity target, ServerLevel level, String teamName, ChatFormatting color, int durationTicks) {
        if (durationTicks <= 0) return;

        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, durationTicks, 0, false, false, true));

        Scoreboard scoreboard = level.getScoreboard();
        PlayerTeam team = ensureTeam(scoreboard, teamName, color);

        String entry = target.getScoreboardName();
        CompoundTag data = target.getPersistentData();

        if (!data.contains(KEY_PREV_TEAM)) {
            PlayerTeam current = scoreboard.getPlayersTeam(entry);
            data.putString(KEY_PREV_TEAM, current == null ? "" : current.getName());
        }

        String activeTeam = data.getString(KEY_ACTIVE_TEAM);
        if (!activeTeam.isEmpty() && !activeTeam.equals(teamName)) {
            PlayerTeam prevActive = scoreboard.getPlayerTeam(activeTeam);
            if (prevActive != null && prevActive == scoreboard.getPlayersTeam(entry)) {
                scoreboard.removePlayerFromTeam(entry, prevActive);
            }
        }

        scoreboard.addPlayerToTeam(entry, team);
        data.putString(KEY_ACTIVE_TEAM, teamName);
        data.putLong(KEY_UNTIL, level.getGameTime() + durationTicks);
    }

    public static void tickRestoreIfExpired(LivingEntity entity, ServerLevel level) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(KEY_UNTIL) || !data.contains(KEY_ACTIVE_TEAM) || !data.contains(KEY_PREV_TEAM)) return;

        long until = data.getLong(KEY_UNTIL);
        if (level.getGameTime() <= until) return;

        Scoreboard scoreboard = level.getScoreboard();
        String entry = entity.getScoreboardName();
        String activeTeamName = data.getString(KEY_ACTIVE_TEAM);
        String prevTeamName = data.getString(KEY_PREV_TEAM);

        if (!activeTeamName.isEmpty()) {
            PlayerTeam activeTeam = scoreboard.getPlayerTeam(activeTeamName);
            if (activeTeam != null && activeTeam == scoreboard.getPlayersTeam(entry)) {
                scoreboard.removePlayerFromTeam(entry, activeTeam);
            }
        }

        if (!prevTeamName.isEmpty()) {
            PlayerTeam prevTeam = scoreboard.getPlayerTeam(prevTeamName);
            if (prevTeam != null) {
                scoreboard.addPlayerToTeam(entry, prevTeam);
            }
        }

        data.remove(KEY_PREV_TEAM);
        data.remove(KEY_ACTIVE_TEAM);
        data.remove(KEY_UNTIL);
    }

    private static PlayerTeam ensureTeam(Scoreboard scoreboard, String teamName, ChatFormatting color) {
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);
        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
        }
        team.setColor(color);
        return team;
    }
}

