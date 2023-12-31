package aedbia.showKey;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;

@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig {
    static final ModConfigSpec SPEC;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue UI_SCALE;
    private static final Map<KeyMapping, ModConfigSpec.BooleanValue> hideKey = new HashMap<>();
    private static final ModConfigSpec.ConfigValue<List<String>> KEYMAPPING_BLACK_LIST;
    private static final ModConfigSpec.ConfigValue<List<String>> KEYMAPPING_WHITE_LIST;
    public static double UIScaleNumber;
    public static Map<KeyMapping, Boolean> hideKeyValue = new HashMap<>();
    public static List<String> keyMappingBlackList = new ArrayList<>();
    public static List<String> keyMappingWhiteList = new ArrayList<>();

    static {
        List<String> list = new ArrayList<>();
        list.add("example0");
        list.add("example1");
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI.UI_Scale", 0.5, 0.1, 1.5);
        KEYMAPPING_BLACK_LIST = BUILDER
                .comment("This is a black list for keymappings that you don't want display. You need put keymapping's name into this;")
                .define("key.black_list.keymapping_black_list", list);
        KEYMAPPING_WHITE_LIST = BUILDER
                .comment("This is a white list for keymappings that you want display. You need put keymapping's name into this;")
                .define("key.black_list.keymapping_white_list", new ArrayList<>());
        BUILDER.configure(ShowKeyConfig::new);
        SPEC = BUILDER.build();
    }

    ShowKeyConfig(ModConfigSpec.Builder builder) {
        for (int x = 0; x < 50 && x < Minecraft.getInstance().options.keyMappings.length; x++) {
            KeyMapping keyMapping = Arrays.stream(Minecraft.getInstance().options.keyMappings).toList().get(x);
            String a = Component.translatable(keyMapping.getName()).getString();
            final ModConfigSpec.BooleanValue booleanValue = builder.comment("Hide '" + a + "'?").define(keyMapping.getName(), KeyInfoHelper.containKeys(keyMapping));
            hideKey.put(keyMapping, booleanValue);
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        keyMappingBlackList = KEYMAPPING_BLACK_LIST.get();
        keyMappingWhiteList = KEYMAPPING_WHITE_LIST.get();
        UIScaleNumber = UI_SCALE.get();
        if (!hideKey.isEmpty()) {
            for (KeyMapping keyMapping : hideKey.keySet()) {
                hideKeyValue.put(keyMapping, hideKey.get(keyMapping).get());
            }
        }
    }
}
