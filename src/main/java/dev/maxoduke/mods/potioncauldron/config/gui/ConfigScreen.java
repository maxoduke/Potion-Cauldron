package dev.maxoduke.mods.potioncauldron.config.gui;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen
{
    private static final Component TITLE = Component.translatable("config.title");
    private static final Component NOTICE_ICON = Component.literal("(?)").withStyle(
        Style.EMPTY.applyFormat(ChatFormatting.UNDERLINE)
    );
    private static final Component NOTICE_TOOLTIP = Component.translatable("config.notice.tooltip");

    private static final Component SAVE = Component.translatable("config.text.save");
    private static final Component RESET = Component.translatable("config.text.reset");
    private static final Component CANCEL = Component.translatable("config.text.cancel");

    private final Screen parent;
    private final ServerConfig currentConfig;
    private final ServerConfig configToChange;

    private StringWidget title;
    private StringWidget noticeIcon;
    private ConfigList configList;
    private Button saveButton;
    private Button resetButton;
    private Button cancelButton;

    private ConfigScreen(Screen parent, ServerConfig currentConfig, ServerConfig configToChange)
    {
        super(TITLE);

        this.parent = parent;
        this.currentConfig = currentConfig;
        this.configToChange = configToChange;
    }

    protected void init()
    {
        super.init();

        title = new StringWidget(width / 2 - 75, 20, 150, 10, TITLE, font);

        noticeIcon = new StringWidget(width - 30, 20, 20, 10, NOTICE_ICON, font);
        noticeIcon.setTooltip(Tooltip.create(NOTICE_TOOLTIP));

        configList = new ConfigList(this, font, configToChange, this::updateButtonStates);

        saveButton = Button.builder(SAVE, this::saveButtonClicked)
            .size(100, 20)
            .pos(width / 2 - 160, height - 35)
            .build();

        resetButton = Button.builder(RESET, this::resetButtonClicked)
            .size(100, 20)
            .pos(width / 2 - 50, height - 35)
            .build();

        cancelButton = Button.builder(CANCEL, this::cancelButtonClicked)
            .size(100, 20)
            .pos(width / 2 + 60, height - 35)
            .build();

        addWidget(title);
        addWidget(noticeIcon);
        addWidget(configList);
        addWidget(saveButton);
        addWidget(resetButton);
        addWidget(cancelButton);

        updateButtonStates();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.centeredText(font, TITLE, width / 2, 20, 0xFFFFFF);

        title.extractRenderState(graphics, mouseX, mouseY, partialTick);
        noticeIcon.extractRenderState(graphics, mouseX, mouseY, partialTick);

        configList.extractRenderState(graphics, mouseX, mouseY, partialTick);
        saveButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
        resetButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
        cancelButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void saveButtonClicked(Button ignored)
    {
        configToChange.save();

        if (PotionCauldron.CONFIG_MANAGER.updateServerConfig(configToChange))
            ServerNetworking.sendConfigToAllClients();

        Minecraft.getInstance().setScreenAndShow(parent);
    }

    private void resetButtonClicked(Button ignored)
    {
        Minecraft.getInstance().setScreenAndShow(reset(parent));
    }

    private void cancelButtonClicked(Button ignored)
    {
        Minecraft.getInstance().setScreenAndShow(parent);
    }

    private void updateButtonStates()
    {
        saveButton.active = configToChange.notEquals(currentConfig) && configList.isValid();
        resetButton.active = configToChange.notEquals(ServerConfig.createDefault());
    }

    public static @NotNull ConfigScreen create(Screen parent)
    {
        ServerConfig currentConfig = ServerConfig.load();
        ServerConfig configToChange = currentConfig.copy();

        return new ConfigScreen(parent, currentConfig, configToChange);
    }

    public static @NotNull ConfigScreen reset(Screen parent)
    {
        ServerConfig currentConfig = ServerConfig.load();
        ServerConfig configToChange = ServerConfig.createDefault();

        return new ConfigScreen(parent, currentConfig, configToChange);
    }
}
