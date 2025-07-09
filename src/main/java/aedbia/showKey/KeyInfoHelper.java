package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCondition;
import aedbia.showKey.client.gui.ShowKeyGui;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KeyInfoHelper {
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
    private static final ShowKeyGui gui = new ShowKeyGui();
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
                    InputConstants.getKey("key.keyboard.9"),
                    InputConstants.getKey("key.keyboard.f1"),
                    InputConstants.getKey("key.keyboard.f2"),
                    InputConstants.getKey("key.keyboard.f3"),
                    InputConstants.getKey("key.keyboard.f4"),
                    InputConstants.getKey("key.keyboard.f5"),
                    InputConstants.getKey("key.keyboard.f6"),
                    InputConstants.getKey("key.keyboard.f7"),
                    InputConstants.getKey("key.keyboard.f8"),
                    InputConstants.getKey("key.keyboard.f9"),
                    InputConstants.getKey("key.keyboard.f10"),
                    InputConstants.getKey("key.keyboard.f11"),
                    InputConstants.getKey("key.keyboard.f12")


            };
    public static Map<String, String> keyNames = new HashMap<>();
    public static Map<String, String> categoryNames = new HashMap<>();
    public static Map<String, ShowKeyCondition> KEY_DISPLAY_RULE = new HashMap<>();
    private static ScheduledFuture<?> future = null;
    private static boolean load = false;

    public static boolean defaultDisplayValue(KeyMapping keyMapping) {
        if (keyMapping.isUnbound()) {
            return false;
        }
        return Arrays.stream(keysToCheck).anyMatch(a -> a == keyMapping.getKey());
    }

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
