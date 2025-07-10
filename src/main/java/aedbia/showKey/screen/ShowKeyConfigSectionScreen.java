package aedbia.showKey.screen;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("SpellCheckingInspection")
public final class ShowKeyConfigSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    private static final String SK_LANG_PREFIX = ShowKey.MODID + ".configuration.";
    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String SECTION = LANG_PREFIX + "section";

    public ShowKeyConfigSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
        this.needsRestart = ModConfigSpec.RestartType.NONE;
    }

    public ShowKeyConfigSectionScreen(final Context parentContext, final Screen parent, final Map<String, Object> valueSpecs, final String key, final Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
        super(parentContext, parent, valueSpecs, key, entrySet, title);
        this.needsRestart = ModConfigSpec.RestartType.NONE;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void addOptions() {
        if (list != null) {
            list.children().clear();
            boolean hasUndoableElements = false;

            final List<@Nullable Element> elements = new ArrayList<>();
            for (final UnmodifiableConfig.Entry entry : context.entries()) {
                final String key = entry.getKey();
                final Object rawValue = entry.getRawValue();
                switch (entry.getRawValue()) {
                    case ModConfigSpec.ConfigValue cv -> {
                        var valueSpec = getValueSpec(key);
                        var element = switch (valueSpec) {
                            case ModConfigSpec.ListValueSpec listValueSpec -> createList(key, listValueSpec, cv);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof String ->
                                    createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Integer ->
                                    createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Long ->
                                    createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Double ->
                                    createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Enum<?> ->
                                    createEnumValue(key, valueSpec, (Supplier) cv::getRaw, (Consumer) cv::set);
                            case null -> null;

                            default -> switch (cv) {
                                case ModConfigSpec.BooleanValue value ->
                                        createBooleanValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.IntValue value ->
                                        createIntegerValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.LongValue value ->
                                        createLongValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.DoubleValue value ->
                                        createDoubleValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.EnumValue value ->
                                        createEnumValue(key, valueSpec, (Supplier) value::getRaw, (Consumer) value::set);
                                default -> createOtherValue(key, cv);
                            };
                        };
                        if (element != null) {
                            elements.add(context.filter().filterEntry(context, key, element));
                        }
                    }
                    case UnmodifiableConfig subsection when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig ->
                            elements.add(createSection(key, subconfig, subsection));
                    default -> {
                        var raw = createOtherSection(key, rawValue);
                        if (raw != null) {
                            elements.add(context.filter().filterEntry(context, key, raw));
                        }
                    }
                }
            }
            elements.addAll(createSyntheticValues());

            for (final Element element : elements) {
                if (element != null) {
                    if (element.name() == null) {
                        list.addSmall(new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty(), font), element.getWidget(options));
                    } else {
                        final StringWidget label = new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, element.name(), font).alignLeft();
                        var tl = element.tooltip();
                        if (tl != null) {
                            label.setTooltip(Tooltip.create(tl));
                        }
                        list.addSmall(label, element.getWidget(options));
                    }
                    hasUndoableElements |= element.undoable();
                }
            }

            if (hasUndoableElements && undoButton == null) {
                createUndoButton();
                createResetButton();
            }
        }
    }

    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Nullable
    protected Element createSection(final String key, final UnmodifiableConfig subconfig, final UnmodifiableConfig subsection) {
        if (subconfig.isEmpty()) return null;
        MutableComponent comp0;
        Component comp1;
        if (KeyInfoHelper.keyNames.containsKey(key)) {
            comp0 = Component.translatable(KeyInfoHelper.keyNames.get(key));
            comp1 = comp0;
        } else {
            comp0 = Component.translatable(SK_LANG_PREFIX + key);
            comp1 = getTooltipComponent(key, null);
        }
        return new Element(comp0, comp1,
                Button.builder(Component.translatable(SECTION, Component.translatable(SK_LANG_PREFIX + "edit")),
                                button -> {
                                    if (minecraft != null) {
                                        minecraft.setScreen(sectionCache.computeIfAbsent(key,
                                                k -> new ShowKeyConfigSectionScreen(context, this, subconfig.valueMap(), key, subsection.entrySet(), comp0).rebuild()));
                                    }
                                })
                        .tooltip(Tooltip.create(comp1))
                        .width(Button.DEFAULT_WIDTH)
                        .build(),
                false);
    }
}
