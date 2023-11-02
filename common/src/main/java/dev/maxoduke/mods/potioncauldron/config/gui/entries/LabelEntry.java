package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class LabelEntry extends ConfigList.Entry
{
    private boolean hasError;
    private final int leftIndent;

    private final StringWidget errorIcon;
    private final StringWidget label;

    public LabelEntry(Font font, Component component, int leftIndent)
    {
        this.leftIndent = leftIndent;
        label = new StringWidget(0, 0, 300, 20, component, font).alignLeft();

        hasError = false;
        errorIcon = new StringWidget(ERROR_ICON, font);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        label.setX(left + leftIndent);
        label.setY(top);
        label.render(guiGraphics, mouseX, mouseY, partialTick);

        if (hasError)
        {
            errorIcon.setX(left + 306);
            errorIcon.setY(top + 5);
            errorIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return Collections.emptyList(); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    public boolean isValid()
    {
        return !hasError;
    }

    public void setError(String errorText)
    {
        hasError = true;
        errorIcon.setTooltip(Tooltip.create(Component.literal(errorText)));
    }

    public void clearError()
    {
        hasError = false;
        errorIcon.setTooltip(null);
    }
}
