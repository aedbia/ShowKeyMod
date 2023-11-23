package com.aedbia.showKey.client;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShowKeyCommandThread extends Thread{
    private static final Logger LOGGER = LogUtils.getLogger();
    public ShowKeyCommandThread() {
        super("ShowKeyCommandThread");
        start();
    }
    public static boolean stop = false;
    @SuppressWarnings("FieldMayBeFinal")
    private List<KeyMapping> keyMapping = new ArrayList<>();
    @Override
    public void run() {
        LOGGER.debug("ShowKeyCommandThread"+" start!");
        while (!stop) {
            //this.wait(1);
            for (KeyMapping keyMapping: Minecraft.getInstance().options.keyMappings){
                if(keyMapping.isDown()){
                    if (!this.keyMapping.contains(keyMapping)&&Minecraft.getInstance().player != null) {
                        this.keyMapping.add(keyMapping);
                        Minecraft.getInstance().gui.getChat().addMessage(Component.nullToEmpty(keyMapping.getName()));
                    }
                }else this.keyMapping.remove(keyMapping);
            }
        }
        LOGGER.debug("ShowKeyCommandThread"+" stop!");
    }
}
