package dev.maxoduke.mods.potioncauldron.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class ModMenu implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<@NotNull Screen> getModConfigScreenFactory()
    {
        return ConfigScreen::create;
    }
}
