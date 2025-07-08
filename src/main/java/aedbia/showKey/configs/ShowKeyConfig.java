package aedbia.showKey.configs;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.client.ShowKeyCondition;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig {
    public static final String OFF_HAND_ITEM = ".Bound off hand items";
    public static final String HIDE = ".Hide";
    public static final String CUS_POS = ".custom position";
    public static final String COO = ".coordinate";
    public static final String CON_DIS = ".condition display";
    public static final String EQUIPMENTS = ".Bound equipments";
    public static final String VEHICLES = ".Bound vehicles";
    public static final String SCREENS = ".Bound screens";
    public static final ForgeConfigSpec SPEC;
    public static final String MAIN_HAND_ITEM = ".Bound main hand items";
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.DoubleValue UI_SCALE;
    private static final ForgeConfigSpec.EnumValue<MODE> DISPLAY_MODE;
    public static double UIScaleNumber;
    public static int displayMode = 0;
    public static Map<String, String> keyValuePaths = new HashMap<>();
    public static List<String> keyMappingWhiteList = new ArrayList<>();
    private static final ForgeConfigSpec.ConfigValue<List<String>> KEYMAPPING_WHITE_LIST;

    static {
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI.UI_Scale", 0.5, 0.1, 1.5);
        KEYMAPPING_WHITE_LIST = BUILDER
                .comment("This is a white list for keymappings that you want display. You need put keymapping's name into this;")
                .define("key.black_list.keymapping_white_list", new ArrayList<>());
        DISPLAY_MODE = BUILDER.defineEnum("UI.DisplayMode", MODE.BOTH, MODE.values());
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
        UIScaleNumber = UI_SCALE.get();
        keyMappingWhiteList = KEYMAPPING_WHITE_LIST.get();
    }

    @SuppressWarnings("resource")
    public static void initKeyConfig() {
        File file = new File(String.valueOf(ShowKey.CONFIG_PATCH));
        if (!file.exists()) {
            boolean a = file.mkdirs();
            ShowKey.LOGGER.debug("create config" + a);
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
                    keyConfig.Add(a + HIDE, KeyInfoHelper.defaultDisplayValue(keyMapping), "Hide \"" + a + "\" ?");
                    keyConfig.Add(a + CUS_POS, false, "Enable custom position?");
                    keyConfig.Add(a + COO+".x", 0, "X offset");
                    keyConfig.Add(a + COO+".y", 0, "Y offset");
                    keyConfig.Add(a + CON_DIS, false, "Enable condition display?");
                    List<String> o = new ArrayList<>();
                    o.add("example0");
                    o.add("example1");
                    keyConfig.Add(a + MAIN_HAND_ITEM, o, "If an item name is added, the key is displayed only when the item is in hand");
                    keyConfig.Add(a + OFF_HAND_ITEM, o, "If an item name is added, the key is displayed only when the item is in hand");
                    keyConfig.Add(a + EQUIPMENTS, o, "If an equipment name is added, the key is displayed only when wear the equipment");
                    keyConfig.Add(a + VEHICLES, o, "If an vehicle name is added, the key is displayed only when drive the vehicle");
                    keyConfig.Add(a + SCREENS, o, "If an screen ID is added, the key is displayed only when the screen is opened");
                }
                keyConfig.build();
            }
            KeyConfig.loadAll();
            loadKeyConfigData();
        }
    }

    public static void loadKeyConfigData() {
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
    }

    enum MODE {
        BOTH,
        RIGHT,
        LEFT
    }
}