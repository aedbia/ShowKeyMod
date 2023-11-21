package com.aedbia.showKey;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;
@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue UI_SCALE;
    static final ForgeConfigSpec SPEC;
    public static double UIScaleNumber;
    public static Map<KeyMapping, Boolean> hideKeyValue = new HashMap<>();

    public static Map<KeyMapping, ForgeConfigSpec.BooleanValue> hideKey = new HashMap<>();
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        UIScaleNumber = UI_SCALE.get();
        if(!hideKey.isEmpty()){
            for(KeyMapping keyMapping:hideKey.keySet()){
                hideKeyValue.put(keyMapping,hideKey.get(keyMapping).get());
            }
        }
    }
    ShowKeyConfig(ForgeConfigSpec.Builder builder){
        for(KeyMapping keyMapping: Minecraft.getInstance().options.keyMappings) {
            final ForgeConfigSpec.BooleanValue booleanValue = builder.define(keyMapping.getName(), KeyInfoHelper.containKeys(keyMapping));
            hideKey.put(keyMapping,booleanValue);
        }
    }
    static {
        BUILDER.configure(ShowKeyConfig::new);
        UI_SCALE = BUILDER
                .comment("UI_Scale")
                .defineInRange("UI_Scale", 0.5, 0.1, 1.5);
        SPEC = BUILDER.build();
    }
}
