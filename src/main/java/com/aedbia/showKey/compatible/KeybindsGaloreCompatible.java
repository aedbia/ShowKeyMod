package com.aedbia.showKey.compatible;


import com.aedbia.showKey.ShowKey;
import com.aedbia.showKey.client.gui.ShowKeyGui;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeybindsGaloreCompatible {

    public static Map<InputConstants.Key, KeyMapping> keybindsGaloreBoundKeyList = new HashMap<>();

    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END) {
            //LogUtils.getLogger().debug(Integer.toString(Thread.currentThread().getThreadGroup().activeCount()));
            String modID = ShowKey.MODID;
            List<InterModComms.IMCMessage> stream = InterModComms.getMessages(modID).toList();
            if (!stream.isEmpty()) {
                LogUtils.getLogger().debug(modID + " receive " + stream.size());
            }else {
                return;
            }
            String senderID="keybinds_galore";
            if (ModList.get().isLoaded(senderID)) {
                Map<?,?> map = new HashMap<>();
                for (InterModComms.IMCMessage supplier : stream) {
                    if (supplier.senderModId().equals(senderID) &&(supplier.messageSupplier().get() instanceof Map<?, ?> m)) {
                        map = m;
                        break;
                    }
                }
                if(!map.isEmpty()){
                    for (Object obj : map.keySet()) {
                        if (obj instanceof InputConstants.Key && map.get(obj) instanceof KeyMapping) {
                            keybindsGaloreBoundKeyList.put((InputConstants.Key) obj, (KeyMapping) map.get(obj));
                        }
                    }
                }
                if(!keybindsGaloreBoundKeyList.isEmpty()){
                    ShowKeyGui.reDraw = true;
                }
            }
        }
        /*
        }
        */
    }

}
