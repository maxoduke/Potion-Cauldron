package dev.maxoduke.mods.potioncauldron.config.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EditboxEntry extends ConfigList.Entry
{
    private boolean hasError;
    private final int leftIndent;

    private final StringWidget errorIcon;
    private final StringWidget name;
    private final EditBox value;

    public EditboxEntry(Font font, Component component, int leftIndent, boolean isEditable, String value, Consumer<String> onChange)
    {
        this.leftIndent = leftIndent;

        name = new StringWidget(0, 0, 172, 20, component, font).alignLeft();
        this.value = new EditBox(font, 0, 0, 30, 20, EMPTY);

        this.value.setValue(value);
        this.value.setResponder(onChange);
        this.value.setEditable(isEditable);

        hasError = false;
        errorIcon = new StringWidget(ERROR_ICON, font);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        name.setX(left + leftIndent);
        name.setY(top);
        name.render(guiGraphics, mouseX, mouseY, partialTick);

        value.setX(left + 269);
        value.setY(top);
        value.render(guiGraphics, mouseX, mouseY, partialTick);

        if (hasError)
        {
            errorIcon.setX(left + 307);
            errorIcon.setY(top + 5);
            errorIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return List.of(value); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    public void enable(boolean value)
    {
        this.value.setEditable(value);
    }

    public String getValue() { return value.getValue(); }

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
