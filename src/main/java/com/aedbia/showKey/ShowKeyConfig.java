package com.aedbia.showKey;

import com.aedbia.showKey.client.gui.KeyInfoHelper;
import com.aedbia.showKey.mixins.AccessorBooleanValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShowKey.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShowKeyConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue UI_SCALE = BUILDER
            .comment("UI_Scale")
            .defineInRange("UI_Scale", 0.5, 0.1, 1.5);
    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static double UIScaleNumber;

    private final static Map<KeyMapping, ForgeConfigSpec.BooleanValue> hideKey = getHideKey();
    private static Map<KeyMapping, ForgeConfigSpec.BooleanValue> getHideKey(){
        Map<KeyMapping, ForgeConfigSpec.BooleanValue> map = new HashMap<>(){};
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings)
        {
            boolean defaultValue = KeyInfoHelper.containKeys(keyMapping);
            ForgeConfigSpec.BooleanValue booleanValue =BUILDER
                    .comment("Does '"+ Component.translatable(keyMapping.getName()).getString()+"' hide?")
                    .define(keyMapping.getName()+"_hide", defaultValue);
            RegistryValue(booleanValue);
            map.put(keyMapping,booleanValue);
        }
        return map;
    }
    public static Map<KeyMapping,Boolean> hideKeyValue = new HashMap<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        UIScaleNumber = UI_SCALE.get();
        for (KeyMapping keyMapping:hideKey.keySet()){
            ForgeConfigSpec.BooleanValue booleanValue = hideKey.get(keyMapping);
            hideKeyValue.put(keyMapping,booleanValue.get());
        }
    }
    public static void RegistryValue(ForgeConfigSpec.ConfigValue<?> value){
        ((AccessorBooleanValue)value).setSpec(SPEC);
    }
}
