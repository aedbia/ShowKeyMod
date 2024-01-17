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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig {
    public static final String OFF_HAND_ITEM = ".Bound off hand items";
    public static final String HIDE = ".Hide";
    public static final String EQUIPMENTS = ".Bound equipments";
    public static final String VEHICLES = ".Bound vehicles";
    public static final String SCREENS = ".Bound screens";
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.DoubleValue UI_SCALE;
    private static final ForgeConfigSpec.EnumValue<MODE> DISPLAY_MODE;
    public static double UIScaleNumber;
    public static int displayMode = 0;
    public static Map<String, String> keyValuePaths = new HashMap<>();
    public static String MAIN_HAND_ITEM = ".Bound main hand items";

    static {
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI.UI_Scale", 0.5, 0.1, 1.5);
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
    }

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
            if (keyValuePaths.containsKey(name)) {
                String path = keyValuePaths.get(name);
                ShowKeyCondition condition = new ShowKeyCondition();
                KeyConfig.Value<?> flag = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + HIDE);
                if (flag != null) {
                    condition.hide = (boolean) flag.get();
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
                        if (b instanceof String c && !condition.boundMainHandItem.contains(c)) {
                            condition.boundMainHandItem.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> equip = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + EQUIPMENTS);
                if (equip != null && equip.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundMainHandItem.contains(c)) {
                            condition.boundMainHandItem.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> vehicle = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + VEHICLES);
                if (vehicle != null && vehicle.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundMainHandItem.contains(c)) {
                            condition.boundMainHandItem.add(c);
                        }
                    });
                }
                KeyConfig.Value<?> screen = KeyConfig.getValue(ShowKey.CONFIG_PATCH, keyMapping.getCategory(), path + SCREENS);
                if (screen != null && screen.get() instanceof List<?> a) {
                    a.forEach(b -> {
                        if (b instanceof String c && !condition.boundMainHandItem.contains(c)) {
                            condition.boundMainHandItem.add(c);
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