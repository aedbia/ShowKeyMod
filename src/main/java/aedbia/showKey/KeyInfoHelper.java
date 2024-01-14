package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCondition;
import aedbia.showKey.client.gui.ShowKeyGui;
import aedbia.showKey.compatible.keybindsGalore.KeybindsGaloreCompatible;
import aedbia.showKey.configs.KeyConfig;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.HashMap;
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
                    InputConstants.getKey("key.keyboard.9")

            };
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
            if (KeybindsGaloreCompatible.keybindsGaloreBoundKeyList.containsKey(keyMapping.getKey())
                    && KeybindsGaloreCompatible.keybindsGaloreBoundKeyList.get(keyMapping.getKey()) != keyMapping) {
                return false;
            }
            String name = keyMapping.getName();
            if (KEY_DISPLAY_RULE.containsKey(name)) {
                ShowKeyCondition condition = KEY_DISPLAY_RULE.get(name);
                return condition.isActive();
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
                KeyConfig.loadAll();
                ShowKeyConfig.loadKeyConfigData();
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
    public void onRenderBar(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), gui.id(), gui);
    }
}
