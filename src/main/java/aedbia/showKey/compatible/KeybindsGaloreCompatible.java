package aedbia.showKey.compatible;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aedbia.showKey.ShowKey.MODID;
public class KeybindsGaloreCompatible {

    public static Map<InputConstants.Key, KeyMapping> keybindsGaloreBoundKeyList = new HashMap<>();
    public static void getKeybindsGaloreMessage(){
        String modID = MODID;
        String senderID="keybinds_galore";
        if (ModList.get().isLoaded(senderID)) {
            List<InterModComms.IMCMessage> stream = InterModComms.getMessages(modID).toList();
            if (!stream.isEmpty()) {
              LogUtils.getLogger().debug(modID + " receive " + stream.size());
            }else {
              return;
            }
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
        }
    }
}
