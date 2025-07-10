package aedbia.showKey.screen;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;

import java.util.Locale;

import static net.neoforged.neoforge.client.gui.ConfigurationScreen.BIG_BUTTON_WIDTH;

@SuppressWarnings("SpellCheckingInspection")
public class ShowkeyConfigScreen extends OptionsSubScreen {

    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String SK_LANG_PREFIX = ShowKey.MODID + ".configuration.";
    private static final String SECTION = LANG_PREFIX + "section";
    private static final MutableComponent EMPTY_LINE = Component.literal("\n\n");
    private static final String FILENAME_TOOLTIP = LANG_PREFIX + "filenametooltip";
    private static final ChatFormatting FILENAME_TOOLTIP_STYLE = ChatFormatting.GRAY;
    private final ModContainer mod;
    private final ModConfig.Type type;
    private final boolean addAddtionButton;
    private boolean customDisplayName = false;

    public ShowkeyConfigScreen(ModContainer mod, Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable(mod.getModId() + ".configuration.title", mod.getModInfo().getDisplayName()));
        this.mod = mod;
        this.type = ModConfig.Type.CLIENT;
        addAddtionButton = true;
    }

    public ShowkeyConfigScreen(ModContainer mod, Screen parent, ModConfig.Type type, Component title) {
        super(parent, Minecraft.getInstance().options, title);
        this.mod = mod;
        this.type = type;
        addAddtionButton = type == ModConfig.Type.CLIENT;
    }

    private static Component getTranslationKeyOfKeyCategory(String fileName) {
        if (KeyInfoHelper.categoryNames.containsKey(fileName)) {
            return Component.translatable(KeyInfoHelper.categoryNames.get(fileName));
        }
        return Component.literal(fileName);
    }

    @Override
    protected void addOptions() {
        Button btn = null;
        for (final ModConfig modConfig : ModConfigs.getConfigSet(type)) {
            if (modConfig.getModId().equals(ShowKey.MODID)) {
                Component component;
                Component component1;
                if (customDisplayName) {
                    component = Component.translatable(SECTION, getTranslationKeyOfKeyCategory(modConfig.getFileName()));
                    component1 = component;
                } else {
                    component = Component.translatable(SECTION, Component.translatable(LANG_PREFIX + "type." + modConfig.getType().name().toLowerCase(Locale.ROOT), mod.getModInfo().getDisplayName()));
                    component1 = Component.translatable(modConfig.getFileName());
                }
                btn = Button.builder(component,
                        button -> {
                            if (minecraft != null) {
                                minecraft.setScreen(new ShowKeyConfigSectionScreen(this, ModConfig.Type.CLIENT, modConfig, component));
                            }
                        }).width(BIG_BUTTON_WIDTH).build();
                MutableComponent tooltip = Component.empty();
                tooltip.append(Component.translatable(FILENAME_TOOLTIP, modConfig.getFileName()).withStyle(FILENAME_TOOLTIP_STYLE));
                btn.setTooltip(Tooltip.create(tooltip));
                if (list != null) {
                    list.addSmall(btn, null);
                }
            }
        }
        if (addAddtionButton) {
            Component ccc = Component.translatable(SECTION, Component.translatable(SK_LANG_PREFIX + "configurekeymapping"));
            btn = Button.builder(ccc,
                    button -> {
                        if (minecraft != null) {
                            var screen = new ShowkeyConfigScreen(mod, this, ModConfig.Type.STARTUP, ccc);
                            screen.customDisplayName = true;
                            minecraft.setScreen(screen);
                        }
                    }).width(BIG_BUTTON_WIDTH).build();
            MutableComponent tooltip = Component.empty();
            tooltip.append(Component.translatable(SK_LANG_PREFIX + "addition").withStyle(FILENAME_TOOLTIP_STYLE));
            btn.setTooltip(Tooltip.create(tooltip));
            if (list != null) {
                list.addSmall(btn, null);
            }
        }

    }
}
