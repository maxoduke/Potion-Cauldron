package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void extractContent(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        final int left = super.getX();
        final int top = super.getY();

        emptyWidget.setX(left);
        emptyWidget.setY(top);
        emptyWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return Collections.emptyList(); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }
}