package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCondition;
import aedbia.showKey.client.gui.ShowKeyGui;
import aedbia.showKey.configs.ShowKeyConfig;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KeyInfoHelper {
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
    private static final ShowKeyGui gui = new ShowKeyGui();
    public static Map<String, String> keyNames = new HashMap<>();
    public static Map<String, String> categoryNames = new HashMap<>();
    public static Map<String, ShowKeyCondition> KEY_DISPLAY_RULE = new HashMap<>();
    private static ScheduledFuture<?> future = null;
    private static boolean load = false;

    public static boolean isShowKeyMapping(KeyMapping keyMapping) {
        if (keyMapping.isUnbound()) {
            return false;
        } else {
            List<String> list = ShowKeyConfig.keyMappingWhiteList;
            if (list.isEmpty() || list.contains(keyMapping.getName())) {
                String name = keyMapping.getName();
                if (KEY_DISPLAY_RULE.containsKey(name)) {
                    ShowKeyCondition condition = KEY_DISPLAY_RULE.get(name);
                    return condition.isActive();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }


    private static void modTick() {
        gui.tick();
        if (gui.onRender) {
            gui.onRender = false;
            if (!load) {
                load = true;
            }
        } else {
            load = false;
        }
    }

    public static void start() {
        if (future == null) {
            future = scheduled.scheduleAtFixedRate(KeyInfoHelper::modTick, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    @SubscribeEvent
    public void onRenderBar(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.HOTBAR, gui.id(), gui);
    }
}
