package com.xiyue.trimmod.common.trim.api;

import net.minecraft.network.chat.Component;

public record TrimTopDisplayData(Component title, Component attribute, int titleColorStart, int titleColorEnd) {
}
