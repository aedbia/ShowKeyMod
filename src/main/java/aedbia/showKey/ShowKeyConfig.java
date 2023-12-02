package aedbia.showKey;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue UI_SCALE;
    static final ForgeConfigSpec SPEC;
    public static double UIScaleNumber;
    public static Map<KeyMapping, Boolean> hideKeyValue = new HashMap<>();

    private static final Map<KeyMapping, ForgeConfigSpec.BooleanValue> hideKey = new HashMap<>();

    private static final ForgeConfigSpec.ConfigValue<List<String>> KEYMAPPING_BLACK_LIST;
    public static List<String> keyMappingBlackList = new ArrayList<>();
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        keyMappingBlackList = KEYMAPPING_BLACK_LIST.get();
        UIScaleNumber = UI_SCALE.get();
        if(!hideKey.isEmpty()){
            for(KeyMapping keyMapping:hideKey.keySet()){
                hideKeyValue.put(keyMapping,hideKey.get(keyMapping).get());
            }
        }
    }
    ShowKeyConfig(ForgeConfigSpec.Builder builder){
        for(int x =0;x<50&&x<Minecraft.getInstance().options.keyMappings.length;x++) {
            KeyMapping keyMapping = Arrays.stream(Minecraft.getInstance().options.keyMappings).toList().get(x);
            String a = Component.translatable(keyMapping.getName()).getString();
            final ForgeConfigSpec.BooleanValue booleanValue = builder.comment("Hide '"+ a +"'?").define(keyMapping.getName(), KeyInfoHelper.containKeys(keyMapping));
            hideKey.put(keyMapping,booleanValue);
        }
    }

    static {
        List<String> list = new ArrayList<>();
        list.add("example0");
        list.add("example1");
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI.UI_Scale", 0.5, 0.1, 1.5);
        KEYMAPPING_BLACK_LIST=BUILDER
                .comment("This is a black list for keymappings that you don't want display. You need put keymapping's name into this;")
                .define("key.black_list.keymapping_black_list",list);
        BUILDER.configure(ShowKeyConfig::new);
        SPEC = BUILDER.build();
    }
}
