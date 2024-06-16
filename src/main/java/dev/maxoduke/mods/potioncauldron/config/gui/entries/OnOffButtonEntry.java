package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class OnOffButtonEntry extends ConfigList.Entry
{
    private final StringWidget label;
    private final CycleButton<Boolean> cycleButton;

    public OnOffButtonEntry(Font font, Component component, boolean selected, CycleButton.OnValueChange<Boolean> onChange)
    {
        label = new StringWidget(0, 0, 270, 20, component, font).alignLeft();
        cycleButton = CycleButton
            .onOffBuilder(selected)
            .displayOnlyValue()
            .create(0, 0, 34, 20, ConfigList.Entry.EMPTY, onChange);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        label.setX(left);
        label.setY(top);
        label.render(guiGraphics, mouseX, mouseY, partialTick);

        cycleButton.setX(left + 267);
        cycleButton.setY(top);
        cycleButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    @NotNull
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    @Override
    @NotNull
    public List<? extends GuiEventListener> children() { return List.of(cycleButton); }
}
