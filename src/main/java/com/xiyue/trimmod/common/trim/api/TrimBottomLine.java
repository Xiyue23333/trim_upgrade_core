package com.xiyue.trimmod.common.trim.api;

import net.minecraft.network.chat.Component;

public record TrimBottomLine(Component text, int colorStart, int colorEnd, boolean streamer) {
    public static TrimBottomLine streamer(Component text, int colorStart, int colorEnd) {
        return new TrimBottomLine(text, colorStart, colorEnd, true);
    }

    public static TrimBottomLine body(Component text) {
        return new TrimBottomLine(text, 0xFFFFFF, 0xFFFFFF, false);
    }
}
