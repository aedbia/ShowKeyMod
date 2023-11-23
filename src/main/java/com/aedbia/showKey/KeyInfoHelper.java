package com.aedbia.showKey;

import com.aedbia.showKey.client.gui.ShowKeyGui;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> KeyMappingNames = new ArrayList<>();
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
        if(ShowKeyConfig.keyMappingBlackList.contains(keyMapping.getName())){
            return false;
        }else if(ShowKeyConfig.hideKeyValue.containsKey(keyMapping)){
            return !ShowKeyConfig.hideKeyValue.get(keyMapping);
        }
        return true;
    }
    static {
        for (KeyMapping keyMapping: Minecraft.getInstance().options.keyMappings){
            KeyMappingNames.add(keyMapping.getName());
        }
    }
    @SuppressWarnings({"NoTranslation", "unused"})
    public static List<InputConstants.Key> AddAllKeyNames(){
        List<InputConstants.Key> list = new ArrayList<>();
        list.add(InputConstants.getKey("key.keyboard.unknown"));
        list.add(InputConstants.getKey("key.mouse.left"));
        list.add(InputConstants.getKey("key.mouse.right"));
        list.add(InputConstants.getKey("key.mouse.middle"));
        list.add(InputConstants.getKey("key.mouse.4"));
        list.add(InputConstants.getKey("key.mouse.5"));
        list.add(InputConstants.getKey("key.mouse.6"));
        list.add(InputConstants.getKey("key.mouse.7"));
        list.add(InputConstants.getKey("key.mouse.8"));
        list.add(InputConstants.getKey("key.keyboard.0"));
        list.add(InputConstants.getKey("key.keyboard.1"));
        list.add(InputConstants.getKey("key.keyboard.2"));
        list.add(InputConstants.getKey("key.keyboard.3"));
        list.add(InputConstants.getKey("key.keyboard.4"));
        list.add(InputConstants.getKey("key.keyboard.5"));
        list.add(InputConstants.getKey("key.keyboard.6"));
        list.add(InputConstants.getKey("key.keyboard.7"));
        list.add(InputConstants.getKey("key.keyboard.8"));
        list.add(InputConstants.getKey("key.keyboard.9"));
        list.add(InputConstants.getKey("key.keyboard.a"));
        list.add(InputConstants.getKey("key.keyboard.b"));
        list.add(InputConstants.getKey("key.keyboard.c"));
        list.add(InputConstants.getKey("key.keyboard.d"));
        list.add(InputConstants.getKey("key.keyboard.e"));
        list.add(InputConstants.getKey("key.keyboard.f"));
        list.add(InputConstants.getKey("key.keyboard.g"));
        list.add(InputConstants.getKey("key.keyboard.h"));
        list.add(InputConstants.getKey("key.keyboard.i"));
        list.add(InputConstants.getKey("key.keyboard.j"));
        list.add(InputConstants.getKey("key.keyboard.k"));
        list.add(InputConstants.getKey("key.keyboard.l"));
        list.add(InputConstants.getKey("key.keyboard.m"));
        list.add(InputConstants.getKey("key.keyboard.n"));
        list.add(InputConstants.getKey("key.keyboard.o"));
        list.add(InputConstants.getKey("key.keyboard.p"));
        list.add(InputConstants.getKey("key.keyboard.q"));
        list.add(InputConstants.getKey("key.keyboard.r"));
        list.add(InputConstants.getKey("key.keyboard.s"));
        list.add(InputConstants.getKey("key.keyboard.t"));
        list.add(InputConstants.getKey("key.keyboard.u"));
        list.add(InputConstants.getKey("key.keyboard.v"));
        list.add(InputConstants.getKey("key.keyboard.w"));
        list.add(InputConstants.getKey("key.keyboard.x"));
        list.add(InputConstants.getKey("key.keyboard.y"));
        list.add(InputConstants.getKey("key.keyboard.z"));
        list.add(InputConstants.getKey("key.keyboard.f1"));
        list.add(InputConstants.getKey("key.keyboard.f2"));
        list.add(InputConstants.getKey("key.keyboard.f3"));
        list.add(InputConstants.getKey("key.keyboard.f4"));
        list.add(InputConstants.getKey("key.keyboard.f5"));
        list.add(InputConstants.getKey("key.keyboard.f6"));
        list.add(InputConstants.getKey("key.keyboard.f7"));
        list.add(InputConstants.getKey("key.keyboard.f8"));
        list.add(InputConstants.getKey("key.keyboard.f9"));
        list.add(InputConstants.getKey("key.keyboard.f10"));
        list.add(InputConstants.getKey("key.keyboard.f11"));
        list.add(InputConstants.getKey("key.keyboard.f12"));
        //list.add(InputConstants.getKey("key.keyboard.f13"));
        //list.add(InputConstants.getKey("key.keyboard.f14"));
        //list.add(InputConstants.getKey("key.keyboard.f15"));
        //list.add(InputConstants.getKey("key.keyboard.f16"));
        //list.add(InputConstants.getKey("key.keyboard.f17"));
        //list.add(InputConstants.getKey("key.keyboard.f18"));
        //list.add(InputConstants.getKey("key.keyboard.f19"));
        //list.add(InputConstants.getKey("key.keyboard.f20"));
        //list.add(InputConstants.getKey("key.keyboard.f21"));
        //list.add(InputConstants.getKey("key.keyboard.f22"));
        //list.add(InputConstants.getKey("key.keyboard.f23"));
        //list.add(InputConstants.getKey("key.keyboard.f24"));
        //list.add(InputConstants.getKey("key.keyboard.f25"));
        //list.add(InputConstants.getKey("key.keyboard.num.lock"));
        list.add(InputConstants.getKey("key.keyboard.keypad.0"));
        list.add(InputConstants.getKey("key.keyboard.keypad.1"));
        list.add(InputConstants.getKey("key.keyboard.keypad.2"));
        list.add(InputConstants.getKey("key.keyboard.keypad.3"));
        list.add(InputConstants.getKey("key.keyboard.keypad.4"));
        list.add(InputConstants.getKey("key.keyboard.keypad.5"));
        list.add(InputConstants.getKey("key.keyboard.keypad.6"));
        list.add(InputConstants.getKey("key.keyboard.keypad.7"));
        list.add(InputConstants.getKey("key.keyboard.keypad.8"));
        list.add(InputConstants.getKey("key.keyboard.keypad.9"));
        list.add(InputConstants.getKey("key.keyboard.keypad.add"));
        list.add(InputConstants.getKey("key.keyboard.keypad.decimal"));
        //list.add(InputConstants.getKey("key.keyboard.keypad.enter"));
        list.add(InputConstants.getKey("key.keyboard.keypad.equal"));
        list.add(InputConstants.getKey("key.keyboard.keypad.multiply"));
        list.add(InputConstants.getKey("key.keyboard.keypad.divide"));
        list.add(InputConstants.getKey("key.keyboard.keypad.subtract"));
        list.add(InputConstants.getKey("key.keyboard.down"));
        list.add(InputConstants.getKey("key.keyboard.left"));
        list.add(InputConstants.getKey("key.keyboard.right"));
        list.add(InputConstants.getKey("key.keyboard.up"));
        list.add(InputConstants.getKey("key.keyboard.apostrophe"));
        list.add(InputConstants.getKey("key.keyboard.backslash"));
        list.add(InputConstants.getKey("key.keyboard.comma"));
        list.add(InputConstants.getKey("key.keyboard.equal"));
        list.add(InputConstants.getKey("key.keyboard.grave.accent"));
        list.add(InputConstants.getKey("key.keyboard.left.bracket"));
        list.add(InputConstants.getKey("key.keyboard.minus"));
        list.add(InputConstants.getKey("key.keyboard.period"));
        list.add(InputConstants.getKey("key.keyboard.right.bracket"));
        list.add(InputConstants.getKey("key.keyboard.semicolon"));
        list.add(InputConstants.getKey("key.keyboard.slash"));
        list.add(InputConstants.getKey("key.keyboard.space"));
        list.add(InputConstants.getKey("key.keyboard.tab"));
        list.add(InputConstants.getKey("key.keyboard.left.alt"));
        list.add(InputConstants.getKey("key.keyboard.left.control"));
        list.add(InputConstants.getKey("key.keyboard.left.shift"));
        //list.add(InputConstants.getKey("key.keyboard.left.win"));
        list.add(InputConstants.getKey("key.keyboard.right.alt"));
        list.add(InputConstants.getKey("key.keyboard.right.control"));
        list.add(InputConstants.getKey("key.keyboard.right.shift"));
        //list.add(InputConstants.getKey("key.keyboard.right.win"));
        //list.add(InputConstants.getKey("key.keyboard.enter"));
        //list.add(InputConstants.getKey("key.keyboard.escape"));
        //list.add(InputConstants.getKey("key.keyboard.backspace"));
        //list.add(InputConstants.getKey("key.keyboard.delete"));
        //list.add(InputConstants.getKey("key.keyboard.end"));
        //list.add(InputConstants.getKey("key.keyboard.home"));
        //list.add(InputConstants.getKey("key.keyboard.insert"));
        //list.add(InputConstants.getKey("key.keyboard.page.down"));
        //list.add(InputConstants.getKey("key.keyboard.page.up"));
        //list.add(InputConstants.getKey("key.keyboard.caps.lock"));
        //list.add(InputConstants.getKey("key.keyboard.pause"));
        //list.add(InputConstants.getKey("key.keyboard.scroll.lock"));
        //list.add(InputConstants.getKey("key.keyboard.menu"));
        //list.add(InputConstants.getKey("key.keyboard.print.screen"));
        //list.add(InputConstants.getKey("key.keyboard.world.1"));
        //list.add(InputConstants.getKey("key.keyboard.world.2"));
        return list;
    }
}
