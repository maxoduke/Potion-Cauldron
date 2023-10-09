package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class AddPotionColumnTitlesEntry extends ConfigList.Entry
{
    private static final Component POTION_NAME = Component.translatable("config.text.potion");
    public static final Component PERCENT_SIGN = Component.literal("%");

    private final int leftIndent;
    private final StringWidget potionName;
    private final StringWidget percentage;

    public AddPotionColumnTitlesEntry(Font font, int leftIndent)
    {
        potionName = new StringWidget(0, 0, 152, 20, POTION_NAME, font).alignLeft();
        percentage = new StringWidget(0, 0, 40, 20, PERCENT_SIGN, font).alignLeft();
        this.leftIndent = leftIndent;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        potionName.setX(left + leftIndent);
        potionName.setY(top);
        potionName.render(guiGraphics, mouseX, mouseY, partialTick);

        percentage.setX(left + 185 + leftIndent);
        percentage.setY(top);
        percentage.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return List.of(percentage); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }
}
