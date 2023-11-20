package com.aedbia.showKey.client.gui;

import com.aedbia.showKey.ShowKeyConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInfoHelper {
    @SuppressWarnings("NoTranslation")
    private static final InputConstants.Key[] keysToCheck =
            {

                    InputConstants.getKey("key.keyboard.tab"),
                    InputConstants.getKey("key.keyboard.caps.lock"),
                    InputConstants.getKey("key.keyboard.left.shift"),
                    InputConstants.getKey("key.keyboard.left.control"),
                    InputConstants.getKey("key.keyboard.space"),
                    InputConstants.getKey("key.keyboard.left.alt"),
                    InputConstants.getKey("key.keyboard.w"),
                    InputConstants.getKey("key.keyboard.a"),
                    InputConstants.getKey("key.keyboard.s"),
                    InputConstants.getKey("key.keyboard.d"),
                    InputConstants.getKey("key.keyboard.0"),
                    InputConstants.getKey("key.keyboard.1"),
                    InputConstants.getKey("key.keyboard.2"),
                    InputConstants.getKey("key.keyboard.3"),
                    InputConstants.getKey("key.keyboard.4"),
                    InputConstants.getKey("key.keyboard.5"),
                    InputConstants.getKey("key.keyboard.6"),
                    InputConstants.getKey("key.keyboard.7"),
                    InputConstants.getKey("key.keyboard.8"),
                    InputConstants.getKey("key.keyboard.9")

            };

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean containKeys(KeyMapping keyMapping){
        for(InputConstants.Key key:keysToCheck){
            if(keyMapping.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }
    @SubscribeEvent
    public void onRenderBar(RegisterGuiOverlaysEvent event)
    {

        ShowKeyGui gui = new ShowKeyGui();
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), gui.id(), gui);
    }

    public static boolean isShowKeyMapping(KeyMapping keyMapping){
        return ShowKeyConfig.hideKeyValue.containsKey(keyMapping)&&!ShowKeyConfig.hideKeyValue.get(keyMapping);
    }
}
