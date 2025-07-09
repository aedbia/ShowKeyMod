package aedbia.showKey.configs;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.client.ShowKeyCondition;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Equipable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

import java.awt.*;
import java.util.List;
import java.util.*;

@EventBusSubscriber(modid = ShowKey.MODID)
public class ShowKeyConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue UI_SCALE;
    private static final ModConfigSpec.IntValue DISPLAY_COUNT;
    private static final ModConfigSpec.EnumValue<MODE> DISPLAY_MODE;
    private static final ConfigValue<List<? extends String>> KEYMAPPING_WHITE_LIST;
    public static double UIScaleNumber;
    public static int displayCount;
    public static int displayMode = 0;
    //public static Map<String, String> keyValuePaths = new HashMap<>();
    public static List<String> keyMappingWhiteList = new ArrayList<>();
    public static boolean canLoad = false;
    private static ShowKeyConfig instance;

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
                .defineList("key.keymapping_black_list", oo, () -> "", obj -> true);
        DISPLAY_MODE = BUILDER
                .defineEnum("UI.DisplayMode", MODE.BOTH, MODE.values());
        SPEC = BUILDER.build();
    }

    public ShowKeyConfig() {
        instance = this;
    }

    public static ShowKeyConfig getInstance() {
        return instance;
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
    /*public static void initKeyConfig() {
        File file = new File(String.valueOf(ShowKey.CONFIG_PATCH));
        if (!file.exists()) {
            boolean a = file.mkdirs();
            ShowKey.LOGGER.debug("create config{}", a);
        }
        if (file.exists()) {
            Map<String, List<KeyMapping>> KeyList = new HashMap<>();
            for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                if (!KeyList.containsKey(keyMapping.getCategory())) {
                    KeyList.put(keyMapping.getCategory(), new ArrayList<>());
                }
                KeyList.get(keyMapping.getCategory()).add(keyMapping);
            }
            for (String category : KeyList.keySet()) {
                KeyConfig keyConfig = new KeyConfig(ShowKey.CONFIG_PATCH, category);
                for (KeyMapping keyMapping : KeyList.get(category)) {

                    String a = Component.translatable(keyMapping.getName()).getString().replace(".", " ");
                    keyValuePaths.put(keyMapping.getName(), a);
                    keyConfig.Add(a + HIDE, false, "Hide(隐藏) \"" + a + "\" ?");
                    keyConfig.Add(a + CUS_POS, false, "Enable custom position?\n自定义位置");
                    keyConfig.Add(a + COO+".x", 0, "X offset\nx轴");
                    keyConfig.Add(a + COO+".y", 0, "Y offset\ny轴");
                    keyConfig.Add(a + COO+".y", 0, "Y offset\ny轴");
                    keyConfig.Add(a + CON_DIS, false, "Enable condition display?\n自定义条件显示");
                    keyConfig.Add(a + HIDE_NAME, false, "Hide bound keymapping name?\n隐藏绑定键位名称");
                    keyConfig.Add(a + DRAW_RIGHT, false, "Draw form right to right?\n从右向左绘制");
                    keyConfig.Add(a + DRAW_SIZE, 1.0d, "Draw Size\n绘制大小");
                    List<String> o = new ArrayList<>();
                    o.add("example0");
                    o.add("example1");
                    keyConfig.Add(a + MAIN_HAND_ITEM, o, "If an item name is added, the key is displayed only when the item is in main hand\n添加一个物品名称后，该键位只会在主手持有该物品时显示");
                    keyConfig.Add(a + OFF_HAND_ITEM, o, "If an item name is added, the key is displayed only when the item is in off hand\n添加一个物品名称后，该键位只会在副手持有该物品时显示");
                    keyConfig.Add(a + EQUIPMENTS, o, "If an equipment name is added, the key is displayed only when wear the equipment\n添加一个装备物品名称后，该键位只会在装备该装备时显示");
                    keyConfig.Add(a + VEHICLES, o, "If a vehicle name is added, the key is displayed only when drive the vehicle\n添加一个载具名称后，该键位只会在乘坐该载具时显示");
                    keyConfig.Add(a + SCREENS, o, "If a screen ID is added, the key is displayed only when the screen is opened\n添加一个界面ID后，该键位只会在打开该界面时显示");
                }
                keyConfig.build();
            }
            KeyConfig.loadAll();
            loadKeyConfigData();
        }
    }*/

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

    enum MODE {
        BOTH,
        RIGHT,
        LEFT
    }

    /*public static void loadKeyConfigData() {
        KeyMapping[] keyMappings = Minecraft.getInstance().options.keyMappings;
        for (KeyMapping keyMapping : keyMappings) {
            String name = keyMapping.getName();
            if (!keyValuePaths.isEmpty()&&keyValuePaths.containsKey(name)) {
                String path = keyValuePaths.get(name);
                ShowKeyCondition condition = new ShowKeyCondition();
                KeyConfig.Value<?> flag = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + HIDE);
                if (flag != null) {
                    condition.hide = (boolean) flag.get();
                }
                KeyConfig.Value<?> flag1 = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + CUS_POS);
                if (flag1 != null) {
                    condition.customPosition = (boolean) flag1.get();
                }
                if(condition.coordinate == null){
                    condition.coordinate = new Point(0,0);
                }
                KeyConfig.Value<?> pointX = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + COO +".x");
                if (pointX != null) {
                    condition.coordinate.x = (int) pointX.get();
                }
                KeyConfig.Value<?> pointY = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + COO +".y");
                if (pointY != null) {
                    condition.coordinate.y = (int) pointY.get();
                }
                KeyConfig.Value<?> size1 = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + DRAW_SIZE);
                if (size1 != null) {
                    condition.size = (double) size1.get();
                }
                KeyConfig.Value<?> hn = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + HIDE_NAME);
                if (hn != null) {
                    condition.hideName = (boolean) hn.get();
                }
                KeyConfig.Value<?> dr = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + DRAW_RIGHT);
                if (dr != null) {
                    condition.drawRight = (boolean) dr.get();
                }
                KeyConfig.Value<?> flag2 = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + CON_DIS);
                if (flag2 != null) {
                    condition.conditionDisplay = (boolean) flag2.get();
                }
                KeyConfig.Value<?> main = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + MAIN_HAND_ITEM);
                if (main != null && main.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundMainHandItem.contains(c)) {
                            condition.boundMainHandItem.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> off = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + OFF_HAND_ITEM);
                if (off != null && off.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundOffHandItem.contains(c)) {
                            condition.boundOffHandItem.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> equip = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + EQUIPMENTS);
                if (equip != null && equip.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundEquipment.contains(c)) {
                            condition.boundEquipment.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> vehicle = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + VEHICLES);
                if (vehicle != null && vehicle.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundVehicle.contains(c)) {
                            condition.boundVehicle.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> screen = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + SCREENS);
                if (screen != null && screen.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundScreens.contains(c)) {
                            condition.boundScreens.add(c);
                        }
                    });
                }
                KeyInfoHelper.KEY_DISPLAY_RULE.put(name, condition);
            }
        }
    }*/

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
        private static final Splitter DOT_SPLITTER = Splitter.on(".");
        public static List<KeyConfig> allKeyConfig = new ArrayList<>();
        private final KeyMapping mapping;
        private final BooleanValue HIDE;
        private final BooleanValue CUSPOS;
        private final ConfigValue<Integer> COOX;
        private final ConfigValue<Integer> COOY;
        private final BooleanValue CONDITION;
        private final BooleanValue HIDEN;
        private final BooleanValue DRAWR;
        private final DoubleValue SIZE;
        private final ConfigValue<List<? extends String>> MHI;
        private final ConfigValue<List<? extends String>> OHI;
        private final ConfigValue<List<? extends String>> EQU;
        private final ConfigValue<List<? extends String>> VEH;
        private final ConfigValue<List<? extends String>> SCR;

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
            List<String> o = new ArrayList<>();
            var range = ModConfigSpec.Range.of(0, Integer.MAX_VALUE);
            MHI = builder.comment("If an item name is added, the key is displayed only when the item is in main hand\n添加一个物品名称后，该键位只会在主手持有该物品时显示")
                    .defineList(split(a + MAIN_HAND_ITEM), () -> o, () -> "null", obj -> {
                        if (obj instanceof String str) {
                            if (str.equals("null") || str.equals("example0") || str.equals("example1")) {
                                return true;
                            } else {
                                return BuiltInRegistries.ITEM.stream().anyMatch(item -> item.getDescriptionId().equals(str));
                            }
                        }
                        return true;
                    }, range);
            OHI = builder.comment("If an item name is added, the key is displayed only when the item is in off hand\n添加一个物品名称后，该键位只会在副手持有该物品时显示")
                    .defineList(split(a + OFF_HAND_ITEM), () -> o, () -> "null", obj -> {
                        if (obj instanceof String str) {
                            if (str.equals("null") || str.equals("example0") || str.equals("example1")) {
                                return true;
                            } else {
                                return BuiltInRegistries.ITEM.stream().anyMatch(item -> item.getDescriptionId().equals(str));
                            }
                        }
                        return true;
                    }, range);
            EQU = builder.comment("If an equipment name is added, the key is displayed only when wear the equipment\n添加一个装备物品名称后，该键位只会在装备该装备时显示")
                    .defineList(split(a + EQUIPMENTS), () -> o, () -> "null", obj -> {
                        if (obj instanceof String str) {
                            if (str.equals("null") || str.equals("example0") || str.equals("example1")) {
                                return true;
                            } else {
                                return BuiltInRegistries.ITEM.stream().anyMatch(item -> item instanceof Equipable && item.getDescriptionId().equals(str));
                            }
                        }
                        return true;
                    }, range);
            VEH = builder.comment("If a vehicle name is added, the key is displayed only when drive the vehicle\n添加一个载具名称后，该键位只会在乘坐该载具时显示")
                    .defineList(split(a + VEHICLES), () -> o, () -> "null", obj -> true, range);
            SCR = builder.comment("If a screen ID is added, the key is displayed only when the screen is opened\n添加一个界面ID后，该键位只会在打开该界面时显示")
                    .defineList(split(a + SCREENS), () -> o, () -> "null", obj -> true, range);
            if (allKeyConfig == null) {
                allKeyConfig = new ArrayList<>();
            }
            if (!allKeyConfig.contains(this)) {
                allKeyConfig.add(this);
            }
        }

        private static List<String> split(String path) {
            return Lists.newArrayList(DOT_SPLITTER.split(path));
        }

        protected static void LoadAllKeyConfigs() {
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
            MHI.get().forEach(obj -> {
                if (obj instanceof String str) {
                    if (!condition.boundMainHandItem.contains(str) && !str.contains("example")) {
                        condition.boundMainHandItem.add(str);
                        //ShowKey.LOGGER.debug("111111111111111111111111111111111111111");
                    }
                }
            });
            OHI.get().forEach(obj -> {
                if (obj instanceof String str) {
                    if (!condition.boundOffHandItem.contains(str) && !str.contains("example")) {
                        condition.boundOffHandItem.add(str);
                    }
                }
            });
            EQU.get().forEach(obj -> {
                if (obj instanceof String str) {
                    if (!condition.boundEquipment.contains(str) && !str.contains("example")) {
                        condition.boundEquipment.add(str);
                    }
                }
            });
            VEH.get().forEach(obj -> {
                if (obj instanceof String str) {
                    if (!condition.boundVehicle.contains(str) && !str.contains("example")) {
                        condition.boundVehicle.add(str);
                    }
                }
            });
            SCR.get().forEach(obj -> {
                if (obj instanceof String str) {
                    if (!condition.boundScreens.contains(str) && !str.contains("example")) {
                        condition.boundScreens.add(str);
                    }
                }
            });
            if (KeyInfoHelper.KEY_DISPLAY_RULE.containsKey(name)) {
                KeyInfoHelper.KEY_DISPLAY_RULE.replace(name, condition);
            } else {
                KeyInfoHelper.KEY_DISPLAY_RULE.put(name, condition);
            }
        }
    }
}