package aedbia.showKey.configs;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.client.ShowKeyCondition;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.Tags;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ShowKey.MODID)
public class ShowKeyConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final DoubleValue UI_SCALE;
    private static final ModConfigSpec.IntValue DISPLAY_COUNT;
    private static final ModConfigSpec.EnumValue<MODE> DISPLAY_MODE;
    private static final ConfigValue<List<? extends String>> KEYMAPPING_WHITE_LIST;
    private static final Splitter DOT_SPLITTER = Splitter.on(".");
    public static double UIScaleNumber;
    public static int displayCount;
    public static int displayMode = 0;
    public static List<String> keyMappingWhiteList = new ArrayList<>();
    public static boolean canLoad = false;

    static {
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI.UI_Scale", 0.5, 0.1, 1.5);
        DISPLAY_COUNT = BUILDER
                .comment("DisplayCount")
                .defineInRange("UI.Display_Count", 20, 1, Integer.MAX_VALUE);
        List<String> oo = new ArrayList<>();
        KEYMAPPING_WHITE_LIST = BUILDER
                .comment("This is a white list for keymappings that you want display. You need put keymapping's name into this;")
                .defineList(split("key.keymapping_black_list"), () -> oo, () -> "", obj -> true, ModConfigSpec.Range.of(0, Integer.MAX_VALUE));
        DISPLAY_MODE = BUILDER
                .defineEnum("UI.DisplayMode", MODE.BOTH, MODE.values());
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (DISPLAY_MODE.get() == MODE.BOTH) {
            displayMode = 0;
        } else if (DISPLAY_MODE.get() == MODE.RIGHT) {
            displayMode = 1;
        } else if (DISPLAY_MODE.get() == MODE.LEFT) {
            displayMode = 2;
        }
        displayCount = DISPLAY_COUNT.get();
        UIScaleNumber = UI_SCALE.get();
        KEYMAPPING_WHITE_LIST.get().forEach(obj -> {
            if (obj instanceof String str) {
                if (!keyMappingWhiteList.contains(str)) {
                    keyMappingWhiteList.add(str);
                }
            }
        });
        if (canLoad) {
            KeyConfig.LoadAllKeyConfigs();
        }
    }

    public static void load() {
        KeyConfig.LoadAllKeyConfigs();
    }

    private static String removeChar(String fileName) {
        String b = fileName;
        String a = "/:\"|<>*?\\\\";
        for (char c : a.toCharArray()) {
            b = b.replace(Character.toString(c), "");
        }
        return b;
    }

    public static void RegisterConfig(ModContainer modContainer) {

        Map<String, List<KeyMapping>> KeyList = new HashMap<>();
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (!KeyList.containsKey(keyMapping.getCategory())) {
                KeyList.put(keyMapping.getCategory(), new ArrayList<>());
            }

            KeyList.get(keyMapping.getCategory()).add(keyMapping);
        }
        for (String category : KeyList.keySet()) {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            for (KeyMapping keyMapping : KeyList.get(category)) {
                new KeyConfig(keyMapping, builder);
            }
            var fileName = ShowKey.MODID + "/" + String.format(Locale.ROOT, "%s.toml", removeChar(category));
            var spec = builder.build();
            modContainer.registerConfig(ModConfig.Type.STARTUP, spec, fileName);
            if (!KeyInfoHelper.categoryNames.containsKey(fileName)) {
                KeyInfoHelper.categoryNames.put(fileName, category);
            }
        }
    }

    private static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }

    enum MODE {
        BOTH,
        RIGHT,
        LEFT
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static class KeyConfig {

        public static final String OFF_HAND_ITEM = ".Bound off hand items";
        public static final String HIDE_PATH = ".Hide";
        public static final String CUS_POS = ".custom position";
        public static final String COO = ".coordinate";
        public static final String CON_DIS = ".condition display";
        public static final String EQUIPMENTS = ".Bound equipments";
        public static final String VEHICLES = ".Bound vehicles";
        public static final String SCREENS = ".Bound screens";
        public static final String HIDE_NAME = ".Hide name";
        public static final String DRAW_RIGHT = ".Draw right";
        public static final String DRAW_SIZE = ".Draw size";
        public static final String MAIN_HAND_ITEM = ".Bound main hand items";
        public static List<KeyConfig> allKeyConfig = new ArrayList<>();
        private static ModConfigSpec.Range<Integer> range;
        private final KeyMapping mapping;
        private final BooleanValue HIDE;
        private final BooleanValue CUSPOS;
        private final ConfigValue<Integer> COOX;
        private final ConfigValue<Integer> COOY;
        private final BooleanValue CONDITION;
        private final BooleanValue HIDEN;
        private final BooleanValue DRAWR;
        private final DoubleValue SIZE;
        private final List<BaseSubConfig> subKeyConfigs = new ArrayList<>();

        private KeyConfig(KeyMapping keyMapping, ModConfigSpec.Builder builder) {
            mapping = keyMapping;
            String a = Component.literal(keyMapping.getName()).getString().replace(".", " ");
            if (KeyInfoHelper.keyNames == null) {
                KeyInfoHelper.keyNames = new HashMap<>();
            }
            KeyInfoHelper.keyNames.put(a, keyMapping.getName());
            //keyValuePaths.put(keyMapping.getName(), a);
            HIDE = builder.comment("Hide(隐藏) \"" + a + "\" ?").define(a + HIDE_PATH, false);
            CUSPOS = builder.comment("Enable custom position?\n自定义位置").define(a + CUS_POS, false);
            COOX = builder.comment("X offset\nx轴").define(a + COO + ".x", 0);
            COOY = builder.comment("Y offset\ny轴").define(a + COO + ".y", 0);
            CONDITION = builder.comment("Enable condition display?\n自定义条件显示").define(a + CON_DIS, false);
            HIDEN = builder.comment("Hide bound keymapping name?\n隐藏绑定键位名称").define(a + HIDE_NAME, false);
            DRAWR = builder.comment("Draw form right to right?\n从右向左绘制").define(a + DRAW_RIGHT, false);
            SIZE = builder.comment("Draw Size\n绘制大小").defineInRange(a + DRAW_SIZE, 1.0d, 0.1d, 10d);
            range = ModConfigSpec.Range.of(0, Integer.MAX_VALUE);
            String tooltip = "If an item name is added, the key is displayed only when the item is in main hand\n添加一个物品名称后，该键位只会在主手持有该物品时显示";
            new SubKeyItemStringListConfig(this, builder, new ShowKeyCondition.SubCondition(sc -> matchContent(sc, InteractionHand.MAIN_HAND)), a + MAIN_HAND_ITEM, tooltip, obj -> true);
            tooltip = "If an item name is added, the key is displayed only when the item is in off hand\n添加一个物品名称后，该键位只会在副手持有该物品时显示";
            new SubKeyItemStringListConfig(this, builder, new ShowKeyCondition.SubCondition(sc ->matchContent(sc, InteractionHand.OFF_HAND)), a + OFF_HAND_ITEM, tooltip, obj -> true);
            tooltip = "If an equipment name is added, the key is displayed only when wear the equipment\n添加一个装备物品名称后，该键位只会在装备该装备时显示";
            new SubKeyItemStringListConfig(this, builder, new ShowKeyCondition.SubCondition(sc -> {
                if (sc.list.isEmpty()) {
                    return true;
                } else if (Minecraft.getInstance().player != null) {
                    List<ItemStack> stacks = new ArrayList<>();
                    for (ItemStack stack : Minecraft.getInstance().player.getArmorSlots()) {
                        if (!stack.isEmpty()) {
                            stacks.add(stack);
                        }
                    }
                    if (stacks.isEmpty()) {
                        return sc.list.contains("null");
                    } else {
                        return stacks.stream().anyMatch(o1 -> {
                            if (sc.matchTags) {
                                return o1.getTags().allMatch(str -> sc.list.contains(str.toString()));
                            } else {
                                return sc.list.contains(o1.getDescriptionId());
                            }
                        });
                    }
                }
                return false;
            }), a + EQUIPMENTS, tooltip, obj -> true);
            tooltip = "If a vehicle name is added, the key is displayed only when drive the vehicle\n添加一个载具名称后，该键位只会在乘坐该载具时显示";
            new SubKeyItemStringListConfig(this, builder, new ShowKeyCondition.SubCondition(sc -> {
                if (sc.list.isEmpty()) {
                    return true;
                } else if (Minecraft.getInstance().player != null) {
                    Entity vehicle = Minecraft.getInstance().player.getVehicle();
                    if (vehicle == null) {
                        return sc.list.contains("null");
                    } else if(sc.matchTags) {
                        return vehicle.getTags().stream().anyMatch(vt->sc.list.contains(vt));
                    }else {
                        return sc.list.contains(vehicle.getType().toString());
                    }
                }
                return false;
            }), a + VEHICLES, tooltip, obj -> true);
            tooltip = "If a screen ID is added, the key is displayed only when the screen is opened\n添加一个界面ID后，该键位只会在打开该界面时显示";
            new SubKeyStringListConfig(this, builder, new ShowKeyCondition.SubCondition(sc -> {
                if (sc.list.isEmpty()) {
                    return true;
                } else {
                    Screen screen = Minecraft.getInstance().screen;
                    if (screen == null) {
                        return sc.list.contains("null");
                    } else {
                        return sc.list.contains(screen.getClass().getName());
                    }
                }
            }), a + SCREENS, tooltip, obj -> true);
            if (allKeyConfig == null) {
                allKeyConfig = new ArrayList<>();
            }
            if (!allKeyConfig.contains(this)) {
                allKeyConfig.add(this);
            }
        }
        private static boolean matchContent(ShowKeyCondition.SubCondition sc,InteractionHand hand){
            if (sc.list.isEmpty()) {
                return true;
            } else {
                if (Minecraft.getInstance().player != null) {
                    ItemStack stack = Minecraft.getInstance().player.getItemInHand(hand);
                    if (stack.isEmpty()) {
                        return sc.list.contains("null");
                    } else if (sc.matchTags) {
                        return stack.getTags().anyMatch(str -> sc.list.contains(str.toString()));
                    } else {
                        return sc.list.contains(stack.getDescriptionId());
                    }
                } else {
                    return false;
                }
            }
        }
        private static void LoadAllKeyConfigs() {
            for (final var value : allKeyConfig) {
                value.ApplyCondition();
            }
        }

        private void ApplyCondition() {
            String name = mapping.getName();
            ShowKeyCondition condition = new ShowKeyCondition();
            condition.hide = HIDE.get();
            condition.customPosition = CUSPOS.get();
            condition.coordinate = new Point(COOX.get(), COOY.get());
            condition.conditionDisplay = CONDITION.get();
            condition.hideName = HIDEN.get();
            condition.drawRight = DRAWR.get();
            condition.size = SIZE.get();
            for (var sub : subKeyConfigs) {
                sub.ApplySubCondition(condition);
            }
            if (KeyInfoHelper.KEY_DISPLAY_RULE.containsKey(name)) {
                KeyInfoHelper.KEY_DISPLAY_RULE.replace(name, condition);
            } else {
                KeyInfoHelper.KEY_DISPLAY_RULE.put(name, condition);
            }
        }

        private static abstract class BaseSubConfig {
            ShowKeyCondition.SubCondition subCondition;
            ConfigValue<List<? extends String>> LIST;
            BooleanValue BLACKLIST;
            public BaseSubConfig(KeyConfig parent, ModConfigSpec.Builder builder, ShowKeyCondition.SubCondition subCondition, String path, String tooltips, Predicate<Object> elementValidator) {
                this.subCondition = subCondition;
                List<String> o = new ArrayList<>();
                LIST = builder.comment(tooltips)
                        .defineList(split(path+".list"), () -> o, () -> "null", elementValidator, range);
                BLACKLIST = builder.comment("black list?\n黑名单模式？").define(path+".blacklist_mode",false);
                parent.subKeyConfigs.add(this);
            }

            public abstract void ApplySubCondition(ShowKeyCondition condition);
        }

        private static class SubKeyItemStringListConfig extends SubKeyStringListConfig {
            private final BooleanValue MATCH;

            public SubKeyItemStringListConfig(KeyConfig parent, ModConfigSpec.Builder builder, ShowKeyCondition.SubCondition subCondition, String path, String tooltips, Predicate<Object> elementValidator) {
                super(parent, builder, subCondition, path, tooltips, elementValidator);
                MATCH = builder.comment("Match tags or itemId?\n匹配tags").define(path + ".matchtag", false);
            }

            @Override
            public void ApplySubCondition(ShowKeyCondition condition) {
                subCondition.matchTags = MATCH.get();
                super.ApplySubCondition(condition);
            }
        }

        private static class SubKeyStringListConfig extends BaseSubConfig {
            public SubKeyStringListConfig(KeyConfig parent, ModConfigSpec.Builder builder, ShowKeyCondition.SubCondition subCondition, String path, String tooltips, Predicate<Object> elementValidator) {
                super(parent, builder, subCondition, path, tooltips, elementValidator);
            }

            @Override
            public void ApplySubCondition(ShowKeyCondition condition) {
                subCondition.blackList = BLACKLIST.get();
                List<String> a = new ArrayList<>();
                LIST.get().forEach(obj -> {
                    if (obj instanceof String str) {
                        if (!str.contains("example")) {
                            a.add(str);
                        }
                    }
                });
                subCondition.list = a;
                if (!condition.subConditions.contains(subCondition)) {
                    condition.subConditions.add(subCondition);
                }
            }
        }
    }
}