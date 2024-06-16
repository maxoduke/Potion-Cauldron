package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BlankEntry extends ConfigList.Entry
{
    private final StringWidget emptyWidget;

    public BlankEntry(Font font)
    {
        emptyWidget = new StringWidget(0, 0, 300, 20, EMPTY, font);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        emptyWidget.setX(left);
        emptyWidget.setY(top);
        emptyWidget.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return Collections.emptyList(); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }
}
