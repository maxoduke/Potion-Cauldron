package dev.maxoduke.mods.potioncauldron.config.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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

@SuppressWarnings("DuplicatedCode")
public class AddPotionEntry extends ConfigList.Entry
{
    private static final Component ADD = Component.translatable("config.text.add");
    private static final Component ADD_SIGN = Component.literal("+");

    private boolean hasError;
    private final int leftIndent;

    private final StringWidget errorIcon;
    private final EditBox potionNameEdit;
    private final EditBox potionChanceEdit;
    private final Button addButton;

    public AddPotionEntry(Font font, int leftIndent, Consumer<String> onChange, Button.OnPress onClick)
    {
        potionNameEdit = new EditBox(font, 0, 0, 180, 20, EMPTY);
        potionChanceEdit = new EditBox(font, 0, 0, 40, 20, EMPTY);

        potionNameEdit.setResponder(onChange);
        potionChanceEdit.setResponder(onChange);

        addButton = Button.builder(ADD_SIGN, onClick).tooltip(Tooltip.create(ADD)).size(30, 24).build();

        addButton.active = false;

        hasError = false;
        errorIcon = new StringWidget(ERROR_ICON, font);

        this.leftIndent = leftIndent;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        potionNameEdit.setX(left + leftIndent);
        potionNameEdit.setY(top);
        potionNameEdit.render(guiGraphics, mouseX, mouseY, partialTick);

        potionChanceEdit.setX(left + 185 + leftIndent);
        potionChanceEdit.setY(top);
        potionChanceEdit.render(guiGraphics, mouseX, mouseY, partialTick);

        addButton.setX(left + 228 + leftIndent);
        addButton.setY(top - 2);
        addButton.render(guiGraphics, mouseX, mouseY, partialTick);

        if (hasError)
        {
            errorIcon.setX(left + 265 + leftIndent);
            errorIcon.setY(top + 5);
            errorIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    @NotNull
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    @Override
    @NotNull
    public List<? extends GuiEventListener> children() { return List.of(potionNameEdit, potionChanceEdit, addButton, errorIcon); }

    public String getPotionName() { return potionNameEdit.getValue().trim(); }

    public Double getPotionChance()
    {
        String value = potionChanceEdit.getValue().trim();
        if (value.isEmpty())
            return null;

        return Double.parseDouble(value) / 100;
    }

    public void setError(String errorText)
    {
        hasError = true;
        errorIcon.setTooltip(Tooltip.create(Component.literal(errorText)));

        addButton.active = false;
    }

    public void clearError()
    {
        hasError = false;
        errorIcon.setTooltip(null);

        addButton.active = true;
    }

    public void disableAddButton()
    {
        addButton.active = false;
    }

    public void clear()
    {
        potionNameEdit.setValue("");
        potionChanceEdit.setValue("");

        addButton.active = false;
    }
}
