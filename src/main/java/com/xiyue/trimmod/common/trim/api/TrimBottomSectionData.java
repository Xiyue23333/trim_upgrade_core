package com.xiyue.trimmod.common.trim.api;

import java.util.List;

public record TrimBottomSectionData(List<TrimBottomLine> lines) {
    public TrimBottomSectionData {
        lines = List.copyOf(lines);
    }

    public static TrimBottomSectionData of(TrimBottomLine... lines) {
        return new TrimBottomSectionData(List.of(lines));
    }
}
