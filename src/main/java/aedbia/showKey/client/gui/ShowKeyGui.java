package aedbia.showKey.client.gui;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.ShowKeyConfig;
import aedbia.showKey.compatible.keybindsGalore.KeybindsGaloreCompatible;
import aedbia.showKey.mixins.SKMixins;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowKeyGui implements IGuiOverlay {

    private static final ResourceLocation button_down = new ResourceLocation(ShowKey.MODID, "textures/gui/button_down.png");
    private static final ResourceLocation button_release = new ResourceLocation(ShowKey.MODID, "textures/gui/button_release.png");
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
    private final Minecraft mc = Minecraft.getInstance();
    private final ResourceLocation id;
    private List<KeyMapping> displayKeyMappings = new ArrayList<>();
    private List<KeyMapping> modifierMappings = new ArrayList<>();
    private int activeKeyCount = 10;

    public ShowKeyGui() {
        this.id = new ResourceLocation(ShowKey.MODID, "key");
        scheduled.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unused")
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        double scale = ShowKeyConfig.UIScaleNumber;
        if (scale < 0.1) {
            scale = 0.5;
        }
        double x = 0;
        double wight = (double) screenWidth / 8;
        double height = (double) screenHeight / (3 * scale);
        int count = 0;
        int showCount = (int) (7 / scale);
        double y = (screenHeight / scale - height / showCount);
        boolean right = false;
        guiGraphics.pose().scale((float) scale, (float) scale, 1f);
        for (KeyMapping keyMapping : modifierMappings) {
            if (count >= 2 * showCount) {
                break;
            }
            if (activeKeyCount / 2 >= 1 && count == activeKeyCount / 2) {
                x = screenWidth / scale - wight;
                y = (screenHeight / scale - height / showCount);
                right = true;
            }
            if (keyMapping.getKeyConflictContext().isActive() && keyMapping.getKeyModifier().isActive(keyMapping.getKeyConflictContext())) {
                renderKeyInfo(guiGraphics, keyMapping, x, y, wight, height / showCount, right);
                y -= (height / showCount);
                count++;
            }
        }
        if (count == 0) {
            for (KeyMapping keyMapping : displayKeyMappings) {
                if (count >= 2 * showCount) {
                    break;
                }
                if (activeKeyCount / 2 >= 1 && count == activeKeyCount / 2) {
                    x = screenWidth / scale - wight;
                    y = (screenHeight / scale - height / showCount);
                    right = true;
                }
                if (keyMapping.getKeyConflictContext().isActive()) {
                    renderKeyInfo(guiGraphics, keyMapping, x, y, wight, height / showCount, right);
                    y -= (height / showCount);
                    count++;
                }
            }
        }
        activeKeyCount = count;
        guiGraphics.pose().scale((1 / (float) scale), (1 / (float) scale), 1f);
    }

    public void renderKeyInfo(GuiGraphics guiGraphics, KeyMapping keyMapping, double x, double y, double wight, double height, boolean right) {
        String key = keyMapping.getKey().getDisplayName().getString().replaceFirst("key.keyboard.", "");
        if (key.length() <= 1) {
            key = key.toUpperCase();
        }
        boolean isKeyDown = ((SKMixins.AccessorKeyMapping) keyMapping).getIsDown();
        int color = isKeyDown ? 0x808080 : 0xFFFFFF;
        String keyName = Component.translatable(keyMapping.getName()).getString();
        int keyLoc = (int) (right ? (x + wight * 3 / 4) : (x + wight / 4));
        int texLoc = (int) (right ? (x + wight / 2) : x);
        int keyNameLoc = (int) (right ? (x - mc.font.width(keyName) + wight / 2) : (x + wight / 2));
        guiGraphics.blit(isKeyDown ? button_down : button_release, texLoc, (int) y, 0, 0, (int) (wight / 2) - 2, (int) height - 2, (int) (wight / 2) - 2, (int) height - 2);
        guiGraphics.drawCenteredString(mc.font, key, keyLoc, (int) y, color);
        guiGraphics.drawString(mc.font, keyName, keyNameLoc, (int) y, color);

    }

    public ResourceLocation id() {
        return this.id;
    }

    private void tick() {
        KeybindsGaloreCompatible.receiveIMCMessage();
        List<KeyMapping> list = Arrays.stream(mc.options.keyMappings).filter(KeyInfoHelper::isShowKeyMapping).collect(Collectors.toMap(KeyMapping::getKey, Function.identity(), (a, b) -> a)).values().stream().toList();
        modifierMappings = list.stream()
                .filter(a -> a.getKeyModifier() != KeyModifier.NONE).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();

        displayKeyMappings = list.stream()
                .filter(a -> a.getKeyModifier() == KeyModifier.NONE).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();
    }
}