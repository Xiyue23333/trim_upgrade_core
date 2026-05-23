package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.client.KeyInit;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import com.xiyue.trimmod.common.trim.dispatcher.TrimScreenDataDispatcher;
import com.xiyue.trimmod.common.trim.set.swamp.SwampSet;
import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;

import java.util.Locale;

public class TrimEffectScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/trim_screen.png");
    private final int imageWidth = 176, imageHeight = 214;
    private int leftPos, topPos;


    private double scrollAmount = 0;
    private int totalBottomHeight = 80;
    private final int viewportHeight = 33; // visible height (matches scissor area: 173..206)
    private boolean isDragging = false;

    private static final float BOTTOM_SCALE = 0.75f;
    private static final int BOTTOM_LINE_HEIGHT = 11;
    private static final int BOTTOM_TEXT_LEFT = 12;
    private static final int BOTTOM_TEXT_RIGHT = 156; // leave room for the scrollbar (starts at x=158)
    private static final float BOTTOM_WRAP_WIDTH_FACTOR = 1.0f; // use full width (avoid unintended extra wrapping)

    // Softer glow (still keeps the streamer effect)
    private static final int STREAMER_GLOW_ALPHA = 0x28;
    private static final int RAINBOW_GLOW_ALPHA = 0x14;

    private static Component tr(String key, Object... args) {
        return Component.translatable(key, args);
    }

    private static String fmt1(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String fmt2(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private int bottomTextMaxWidth(float scale) {
        return Math.max(10, Mth.floor(((BOTTOM_TEXT_RIGHT - BOTTOM_TEXT_LEFT) / scale) * BOTTOM_WRAP_WIDTH_FACTOR));
    }

    private static String normalizeGuiText(String text) {
        if (text == null) return "";
        String normalized = text.replace("\uFEFF", "");
        normalized = normalized.replace("\r\n", "\n").replace('\r', '\n');
        return normalized.stripLeading();
    }

    private int drawWrappedLines(GuiGraphics guiGraphics, Component text, int x, int y, int maxWidth, int color) {
        String normalized = normalizeGuiText(text.getString());
        if (normalized.isBlank()) return 0;

        int used = 0;
        String[] paragraphs = normalized.split("\n", -1);
        for (String paragraph : paragraphs) {
            String lineText = paragraph.stripLeading();
            if (lineText.isBlank()) {
                used++;
                continue;
            }

            var lines = this.font.split(Component.literal(lineText), maxWidth);
            if (lines.isEmpty()) {
                guiGraphics.drawString(this.font, lineText, x, y + used * BOTTOM_LINE_HEIGHT, color, false);
                used++;
                continue;
            }

            for (int i = 0; i < lines.size(); i++) {
                guiGraphics.drawString(this.font, lines.get(i), x, y + (used + i) * BOTTOM_LINE_HEIGHT, color, false);
            }
            used += lines.size();
        }
        return Math.max(1, used);
    }

    private int bottomHeightPxFromLineSlots(int lineSlots) {
        int height = Mth.ceil(lineSlots * BOTTOM_LINE_HEIGHT * BOTTOM_SCALE) + 6;
        return Math.max(viewportHeight, height);
    }

    public TrimEffectScreen() {
        super(tr("gui.trimupgrade.trim_effect.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, totalBottomHeight - viewportHeight);
        this.scrollAmount = Mth.clamp(this.scrollAmount - delta * 12, 0, maxScroll);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int trackX = leftPos + 158;
            int trackY = topPos + 173;
            if (mouseX >= trackX && mouseX <= trackX + 8 && mouseY >= trackY && mouseY <= trackY + 44) {
                this.isDragging = true;
                this.updateScrollFromMouse(mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) this.isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging && totalBottomHeight > viewportHeight) {
            this.updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

/**
 * 根据鼠标位置更新滚动量
 * @param mouseY 鼠标的Y坐标
 */
    private void updateScrollFromMouse(double mouseY) {
        int trackY = topPos + 174;
        int trackHeight = 34;
        int thumbHeight = 13;
        double ratio = Mth.clamp((mouseY - trackY - thumbHeight / 2.0) / (trackHeight - thumbHeight), 0, 1);
        this.scrollAmount = ratio * (totalBottomHeight - viewportHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            renderTopFixedArea(guiGraphics, player);
            renderMidTitle(guiGraphics, player);
            guiGraphics.enableScissor(leftPos + 8, topPos + 173, leftPos + 160, topPos + 206);
            renderScrollingBottomArea(guiGraphics, player);
            guiGraphics.disableScissor();

            drawScrollbarThumb(guiGraphics, mouseX, mouseY);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderMidTitle(GuiGraphics guiGraphics, Player player) {
        String key = null;
        if (isContributorSet(player)) {
            key = "gui.trimupgrade.trim_effect.contributor.mid_title";
        } else if (isSwampSet(player)) {
            key = "gui.trimupgrade.trim_effect.swamp.mid_title";
        }
        if (key == null) return;

        String text = tr(key).getString();
        int x = leftPos + (imageWidth - this.font.width(text)) / 2;
        int y = topPos + 146;
        if ("gui.trimupgrade.trim_effect.swamp.mid_title".equals(key)) {
            drawStreamerText(guiGraphics, text, x, y, 0x2E7D5B, 0x8FD9A8);
        } else {
            drawRainbowStreamerText(guiGraphics, text, x, y);
        }
    }

/**
 * 绘制滚动条滑块的方法
 * @param guiGraphics 图形渲染接口，用于绘制UI元素
 */
    /**
     * 修改后的绘制方法，新增了鼠标坐标参数
     */
    private void drawScrollbarThumb(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (totalBottomHeight <= viewportHeight) return;

        int thumbX = leftPos + 167 - 6;
        int trackY = topPos + 173;
        int trackHeight = 34;
        int thumbHeight = 12;

        float scrollPercent = (float) (scrollAmount / (totalBottomHeight - viewportHeight));
        int thumbPos = (int) (scrollPercent * (trackHeight - thumbHeight));
        int currentThumbY = trackY + thumbPos;

        // 判断状态：是正在拖动，还是鼠标正悬停在滑块方块上
        boolean isHovered = mouseX >= thumbX && mouseX <= thumbX + 8
                && mouseY >= currentThumbY && mouseY <= currentThumbY + thumbHeight;

        // 确定 U 坐标：如果正在拖拽或悬停，使用右侧的新贴图
        int uOffset = (this.isDragging || isHovered) ? 248 : 240;

        guiGraphics.blit(TEXTURE, thumbX, currentThumbY, uOffset, 0, 8, thumbHeight);
    }

    private void renderTopFixedArea(GuiGraphics guiGraphics, Player player) {
        boolean contributorSet = isContributorSet(player);
        boolean swampSet = isSwampSet(player);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = player.getItemBySlot(slots[i]);
            int y = topPos + 12 + (i * 32);

            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, leftPos + 13, y + 1);
                var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);

                if (trim.isPresent()) {
                    int lvl = TrimUtils.getUpgradeLevel(stack);
                    Component title = null;
                    Component attr = null;
                    int titleColorStart = 0xFFFFFF;
                    int titleColorEnd = 0xFFFFFF;

                    boolean contributorOverride = contributorSet
                            && (trim.get().pattern().is(TrimPatterns.WILD)
                            || trim.get().pattern().is(TrimPatterns.WARD)
                            || trim.get().pattern().is(TrimPatterns.COAST)
                            || trim.get().pattern().is(TrimPatterns.TIDE));

                    boolean swampOverride = swampSet
                            && (trim.get().pattern().is(TrimPatterns.TIDE)
                            || trim.get().pattern().is(TrimPatterns.WILD));

                    TrimTopDisplayData topDisplay = swampOverride
                            ? SwampSet.SCREEN.createTopDisplay(lvl, trim.get().pattern())
                            : TrimScreenDataDispatcher.resolveTopDisplay(trim.get().pattern(), lvl, contributorOverride);
                    if (topDisplay != null) {
                        title = topDisplay.title();
                        attr = topDisplay.attribute();
                        titleColorStart = topDisplay.titleColorStart();
                        titleColorEnd = topDisplay.titleColorEnd();
                    } else if (trim.get().pattern().is(TrimPatterns.EYE)) {
                        title = tr("gui.trimupgrade.trim_effect.top.title.eye", lvl);
                        titleColorStart = 0x55FFFF;
                        titleColorEnd = 0xAA00FF;
                        attr = tr("gui.trimupgrade.trim_effect.top.attr.eye", fmt1(1.25 - 0.1875 * lvl), fmt1(0.85 - 0.15 * lvl), fmt1(2.5 + 1.0 * lvl));
                    } else if (trim.get().pattern().is(TrimPatterns.SPIRE)) {
                        title = tr("gui.trimupgrade.trim_effect.top.title.spire", lvl);
                        titleColorStart = 0x1A1A1A;
                        titleColorEnd = 0xAA00AA;
                        double dmgPct = 2.0 + 1.5 * lvl;
                        double reach = 0.05 + 0.05 * lvl;
                        attr = tr("gui.trimupgrade.trim_effect.top.attr.spire", fmt1(dmgPct), fmt2(reach));
                    }

                    if (title != null && attr != null) {
                        drawStreamerText(guiGraphics, title, leftPos + 41, y + 3, titleColorStart, titleColorEnd);
                        var pose = guiGraphics.pose();
                        pose.pushPose();
                        float s = 0.75f;
                        pose.scale(s, s, s);
                        guiGraphics.drawString(this.font, attr, (int) ((leftPos + 41) / s), (int) ((y + 14) / s), 0xFFFFFF, false);
                        pose.popPose();
                    }
                }
            }
        }
    }

    private void renderScrollingBottomArea(GuiGraphics guiGraphics, Player player) {
        int dynamicY = (int) (topPos + 175 - scrollAmount);

        if (isContributorSet(player)) {
            int minLvl = TrimUtils.getMinUpgradeLevel(player);
            int lines = renderContributorBottom(guiGraphics, minLvl, dynamicY);
            this.totalBottomHeight = bottomHeightPxFromLineSlots(lines);
            return;
        }

        if (isSwampSet(player)) {
            int minLvl = TrimUtils.getMinUpgradeLevel(player);
            int lines = renderSwampBottom(guiGraphics, minLvl, dynamicY);
            this.totalBottomHeight = bottomHeightPxFromLineSlots(lines);
            return;
        }

        // 统计套装数量
        int tideCount = 0, wildCount = 0, snoutCount = 0, wayfinderCount = 0, vexCount = 0,
                duneCount = 0, hostCount = 0, coastCount = 0, sentryCount = 0, shaperCount = 0, raiserCount = 0, wardCount = 0, silenceCount = 0, ribCount = 0, eyeCount = 0, spireCount = 0;

        for (ItemStack s : player.getArmorSlots()) {
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), s);
            if (trim.isPresent()) {
                if (trim.get().pattern().is(TrimPatterns.TIDE)) tideCount++;
                else if (trim.get().pattern().is(TrimPatterns.WILD)) wildCount++;
                else if (trim.get().pattern().is(TrimPatterns.SNOUT)) snoutCount++;
                else if (trim.get().pattern().is(TrimPatterns.WAYFINDER)) wayfinderCount++;
                else if (trim.get().pattern().is(TrimPatterns.VEX)) vexCount++;
                else if (trim.get().pattern().is(TrimPatterns.DUNE)) duneCount++;
                else if (trim.get().pattern().is(TrimPatterns.HOST)) hostCount++;
                else if (trim.get().pattern().is(TrimPatterns.COAST)) coastCount++;
                else if (trim.get().pattern().is(TrimPatterns.SENTRY)) sentryCount++;
                else if (trim.get().pattern().is(TrimPatterns.SHAPER)) shaperCount++;
                else if (trim.get().pattern().is(TrimPatterns.RAISER)) raiserCount++;
                else if (trim.get().pattern().is(TrimPatterns.WARD)) wardCount++;
                else if (trim.get().pattern().is(TrimPatterns.SILENCE)) silenceCount++;
                else if (trim.get().pattern().is(TrimPatterns.RIB)) ribCount++;
                else if (trim.get().pattern().is(TrimPatterns.EYE)) eyeCount++;
                else if (trim.get().pattern().is(TrimPatterns.SPIRE)) spireCount++;
            }
        }

        int minLvl = TrimUtils.getMinUpgradeLevel(player);

        int lines = 1;
        if (tideCount >= 4) {
            lines = renderTideBottom(guiGraphics, minLvl, dynamicY);
        } else if (wildCount >= 4) {
            lines = renderWildBottom(guiGraphics, minLvl, dynamicY);
        } else if (snoutCount >= 4) {
            lines = renderSnoutBottom(guiGraphics, minLvl, dynamicY);
        } else if (wayfinderCount >= 4) {
            lines = renderWayfinderBottom(guiGraphics, minLvl, dynamicY);
        } else if (vexCount >= 4) {
            lines = renderVexBottom(guiGraphics, minLvl, dynamicY);
        } else if (duneCount >= 4) {
            lines = renderDuneBottom(guiGraphics, minLvl, dynamicY);
        } else if (hostCount >= 4) {
            lines = renderHostBottom(guiGraphics, minLvl, dynamicY);
        } else if (coastCount >= 4) {
            lines = renderCoastBottom(guiGraphics, minLvl, dynamicY);
        } else if (sentryCount >= 4) {
            lines = renderSentryBottom(guiGraphics, minLvl, dynamicY);
        } else if (shaperCount >= 4) {
            lines = renderShaperBottom(guiGraphics, minLvl, dynamicY);
        } else if (raiserCount >= 4) {
            lines = renderRaiserBottom(guiGraphics, minLvl, dynamicY);
        } else if (wardCount >= 4) {
            lines = renderWardBottom(guiGraphics, minLvl, dynamicY);
        } else if (silenceCount >= 4) {
            lines = renderSilenceBottom(guiGraphics, minLvl, dynamicY);
        } else if (ribCount >= 4) {
            lines = renderRibBottom(guiGraphics, minLvl, dynamicY);
        } else if (eyeCount >= 4) {
            lines = renderEyeBottom(guiGraphics, minLvl, dynamicY);
        } else if (spireCount >= 4) {
            lines = renderSpireBottom(guiGraphics, minLvl, dynamicY);
        }

        else {
            lines = renderSetInactiveBottom(guiGraphics, dynamicY);
        }

        this.totalBottomHeight = bottomHeightPxFromLineSlots(lines);
    }

    // --- 修改后的各套装底部渲染逻辑 (支持 dynamicY) ---

    private int renderSetInactiveBottom(GuiGraphics guiGraphics, int y) {
        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(BOTTOM_SCALE, BOTTOM_SCALE, BOTTOM_SCALE);

        int x = (int) ((leftPos + BOTTOM_TEXT_LEFT) / BOTTOM_SCALE);
        int y0 = (int) ((y + 10) / BOTTOM_SCALE);
        int maxWidth = bottomTextMaxWidth(BOTTOM_SCALE);

        int lines = drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.set_inactive"), x, y0, maxWidth, 0xFFFFFF);
        pose.popPose();
        return lines;
    }

    private int renderContributorBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        String key = KeyInit.SKILL_KEY.getTranslatedKeyMessage().getString().toUpperCase();

        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(BOTTOM_SCALE, BOTTOM_SCALE, BOTTOM_SCALE);

        int x = (int) ((leftPos + BOTTOM_TEXT_LEFT) / BOTTOM_SCALE);
        int y0 = (int) (y / BOTTOM_SCALE);
        int maxWidth = bottomTextMaxWidth(BOTTOM_SCALE);

        int lineSlots = 0;
        drawRainbowStreamerText(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.header"), x, y0);
        lineSlots += 1;

        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.combo"), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);
        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.level", minLevel), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);

        drawStreamerText(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.crouch_title"), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, 0xFFD700, 0x55FF55);
        lineSlots += 1;
        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.crouch_desc"), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);

        drawStreamerText(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.cap_title"), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, 0xFFD700, 0xFF5555);
        lineSlots += 1;
        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.cap_desc"), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);

        drawStreamerText(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.skill_title", key), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, 0xFFD700, 0xFF5555);
        lineSlots += 1;
        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.skill_desc1", 8 + 4 * minLevel), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);
        lineSlots += drawWrappedLines(guiGraphics, tr("gui.trimupgrade.trim_effect.desc.contributor.skill_desc2", Config.cooldownContributorSeconds), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);

        pose.popPose();
        return lineSlots;
    }

    private int renderSwampBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        String key = KeyInit.SKILL_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        TrimBottomSectionData data = SwampSet.SCREEN.createBottomSection(minLevel, key);
        return renderBottomSectionData(guiGraphics, data, y);
    }

    private int renderBottomSection(GuiGraphics guiGraphics, ResourceKey<TrimPattern> pattern, int minLevel, int y) {
        String key = KeyInit.SKILL_KEY.getTranslatedKeyMessage().getString().toUpperCase();
        TrimBottomSectionData data = TrimScreenDataDispatcher.resolveBottomSection(pattern, minLevel, key);
        if (data == null) {
            return renderSetInactiveBottom(guiGraphics, y);
        }
        return renderBottomSectionData(guiGraphics, data, y);
    }

    private int renderBottomSectionData(GuiGraphics guiGraphics, TrimBottomSectionData data, int y) {
        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(BOTTOM_SCALE, BOTTOM_SCALE, BOTTOM_SCALE);

        int x = (int) ((leftPos + BOTTOM_TEXT_LEFT) / BOTTOM_SCALE);
        int y0 = (int) (y / BOTTOM_SCALE);
        int maxWidth = bottomTextMaxWidth(BOTTOM_SCALE);

        int lineSlots = 0;
        for (TrimBottomLine line : data.lines()) {
            if (line.streamer()) {
                drawStreamerText(guiGraphics, line.text(), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, line.colorStart(), line.colorEnd());
                lineSlots += 1;
            } else {
                lineSlots += drawWrappedLines(guiGraphics, line.text(), x, y0 + lineSlots * BOTTOM_LINE_HEIGHT, maxWidth, 0xFFFFFF);
            }
        }

        pose.popPose();
        return lineSlots;
    }

    private int renderTideBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.TIDE, minLevel, y);
    }

    private int renderWildBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.WILD, minLevel, y);
    }

    private int renderSnoutBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.SNOUT, minLevel, y);
    }

    private int renderWayfinderBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.WAYFINDER, minLevel, y);
    }

    private int renderVexBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.VEX, minLevel, y);
    }

    private int renderDuneBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.DUNE, minLevel, y);
    }

    private int renderHostBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.HOST, minLevel, y);
    }

    private int renderCoastBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.COAST, minLevel, y);
    }

    private int renderSentryBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.SENTRY, minLevel, y);
    }

    private int renderShaperBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.SHAPER, minLevel, y);
    }

    private int renderRaiserBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.RAISER, minLevel, y);
    }

    private int renderWardBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.WARD, minLevel, y);
    }

    private int renderSilenceBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.SILENCE, minLevel, y);
    }

    private int renderRibBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.RIB, minLevel, y);
    }

    private int renderEyeBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.EYE, minLevel, y);
    }

    private int renderSpireBottom(GuiGraphics guiGraphics, int minLevel, int y) {
        return renderBottomSection(guiGraphics, TrimPatterns.SPIRE, minLevel, y);
    }

    private void drawStreamerText(GuiGraphics guiGraphics, Component text, int x, int y, int colorStart, int colorEnd) {
        drawStreamerText(guiGraphics, text.getString(), x, y, colorStart, colorEnd);
    }

    private void drawStreamerText(GuiGraphics guiGraphics, String text, int x, int y, int colorStart, int colorEnd) {
        text = normalizeGuiText(text);
        if (text.isBlank()) return;

        float currentX = x;
        float time = (System.currentTimeMillis() % 3000) / 3000.0f;

        for (int i = 0; i < text.length(); i++) {
            String charStr = String.valueOf(text.charAt(i));
            // 计算每个字符的波动偏移
            float wave = (float) Math.sin((time + i * 0.05f) * 2 * Math.PI) * 0.5f + 0.5f;
            int baseColor = interpolateColor(colorStart, colorEnd, wave);

            // 绘制发光层 (外发光效果)
            int glowColor = (baseColor & 0x00FFFFFF) | (STREAMER_GLOW_ALPHA << 24);

            if (text.charAt(i) == '\u25B6') {
                drawTriangleArrow(guiGraphics, (int) currentX, y + 1, glowColor, baseColor);
                currentX += Math.max(6, this.font.width(">"));
                continue;
            }

            if (!Character.isWhitespace(text.charAt(i))) {
                guiGraphics.drawString(this.font, charStr, (int)currentX + 1, y, glowColor, false);
                guiGraphics.drawString(this.font, charStr, (int)currentX - 1, y, glowColor, false);
            }

            // 绘制主文本
            guiGraphics.drawString(this.font, charStr, (int)currentX, y, baseColor, false);
            currentX += this.font.width(charStr);
        }
    }

    private void drawTriangleArrow(GuiGraphics guiGraphics, int x, int y, int glowColor, int baseColor) {
        drawTriangleArrowSolid(guiGraphics, x - 1, y, glowColor);
        drawTriangleArrowSolid(guiGraphics, x + 1, y, glowColor);
        drawTriangleArrowSolid(guiGraphics, x, y, (baseColor & 0x00FFFFFF) | (0xFF << 24));
    }

    private void drawTriangleArrowSolid(GuiGraphics guiGraphics, int x, int y, int argbColor) {
        guiGraphics.fill(x, y + 0, x + 1, y + 1, argbColor);
        guiGraphics.fill(x, y + 1, x + 2, y + 2, argbColor);
        guiGraphics.fill(x, y + 2, x + 3, y + 3, argbColor);
        guiGraphics.fill(x, y + 3, x + 4, y + 4, argbColor);
        guiGraphics.fill(x, y + 4, x + 3, y + 5, argbColor);
        guiGraphics.fill(x, y + 5, x + 2, y + 6, argbColor);
        guiGraphics.fill(x, y + 6, x + 1, y + 7, argbColor);
    }

    private void drawRainbowStreamerText(GuiGraphics guiGraphics, Component text, int x, int y) {
        drawRainbowStreamerText(guiGraphics, text.getString(), x, y);
    }

    private void drawRainbowStreamerText(GuiGraphics guiGraphics, String text, int x, int y) {
        float currentX = x;
        float time = (System.currentTimeMillis() % 4000L) / 4000.0f;

        for (int i = 0; i < text.length(); i++) {
            String charStr = String.valueOf(text.charAt(i));

            float hue = (time + i * 0.12f) % 1.0f;
            float wave = (float) Math.sin((time + i * 0.08f) * 2 * Math.PI) * 0.2f + 0.8f;
            int baseColor = Mth.hsvToRgb(hue, 1.0f, Mth.clamp(wave, 0.0f, 1.0f));

            int glowColor = (baseColor & 0x00FFFFFF) | (RAINBOW_GLOW_ALPHA << 24);
            guiGraphics.drawString(this.font, charStr, (int) currentX + 1, y, glowColor, false);
            guiGraphics.drawString(this.font, charStr, (int) currentX - 1, y, glowColor, false);

            guiGraphics.drawString(this.font, charStr, (int) currentX, y, baseColor, true);
            currentX += this.font.width(charStr);
        }
    }

    private int interpolateColor(int color1, int color2, float factor) {
        int r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;
        int r = (int) (r1 + factor * (r2 - r1)), g = (int) (g1 + factor * (g2 - g1)), b = (int) (b1 + factor * (b2 - b1));
        return (r << 16) | (g << 8) | b;
    }

    private boolean isContributorSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) return false;

        var trimHead = ArmorTrim.getTrim(player.level().registryAccess(), head);
        var trimChest = ArmorTrim.getTrim(player.level().registryAccess(), chest);
        var trimLegs = ArmorTrim.getTrim(player.level().registryAccess(), legs);
        var trimFeet = ArmorTrim.getTrim(player.level().registryAccess(), feet);

        if (trimHead.isEmpty() || trimChest.isEmpty() || trimLegs.isEmpty() || trimFeet.isEmpty()) return false;

        return trimHead.get().pattern().is(TrimPatterns.WILD)
                && trimChest.get().pattern().is(TrimPatterns.WARD)
                && trimLegs.get().pattern().is(TrimPatterns.COAST)
                && trimFeet.get().pattern().is(TrimPatterns.TIDE);
    }

    private boolean isSwampSet(Player player) {
        int tideCount = 0;
        int wildCount = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
            if (trim.isPresent()) {
                if (trim.get().pattern().is(TrimPatterns.TIDE)) tideCount++;
                else if (trim.get().pattern().is(TrimPatterns.WILD)) wildCount++;
            }
        }
        return tideCount >= 2 && wildCount >= 2;
    }

    @Override
    public boolean isPauseScreen() { return false; }

}
