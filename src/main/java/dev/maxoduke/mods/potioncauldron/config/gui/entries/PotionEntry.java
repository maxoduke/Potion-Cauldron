package dev.maxoduke.mods.potioncauldron.config.gui.entries;

import dev.maxoduke.mods.potioncauldron.config.gui.ConfigList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PotionEntry extends ConfigList.Entry
{
    private static final Component REMOVE = Component.translatable("config.text.remove");
    private static final Component REMOVE_SIGN = Component.literal("x");

    private final int leftIndent;
    private final String potionName;

    private final StringWidget potionNameWidget;
    private final StringWidget potionChanceWidget;
    private final Button removeButton;

    public PotionEntry(Font font, int leftIndent, String potionName, double potionChance, Consumer<PotionEntry> removeClickedListener)
    {
        this.leftIndent = leftIndent;
        this.potionName = potionName;
        String _potionChance = BigDecimal.valueOf(potionChance * 100).stripTrailingZeros().toPlainString();

        potionNameWidget = new StringWidget(150, 20, Component.literal(potionName), font).alignLeft();
        potionChanceWidget = new StringWidget(40, 20, Component.literal(_potionChance), font).alignLeft();

        removeButton = Button.builder(REMOVE_SIGN, ignored -> removeClickedListener.accept(this))
            .tooltip(Tooltip.create(REMOVE))
            .width(30)
            .build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean bl, float partialTick)
    {
        potionNameWidget.setX(left + 4 + leftIndent);
        potionNameWidget.setY(top);
        potionNameWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        potionChanceWidget.setX(left + 189 + leftIndent);
        potionChanceWidget.setY(top);
        potionChanceWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        removeButton.setX(left + 228 + leftIndent);
        removeButton.setY(top);
        removeButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return List.of(removeButton); }

    @NotNull
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    public String getPotionName() { return potionName; }
}
