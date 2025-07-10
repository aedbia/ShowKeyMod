package aedbia.showKey.client.gui;


import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.client.ShowKeyCondition;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.util.*;
import java.util.stream.Collectors;

public class ShowKeyGui implements LayeredDraw.Layer {

    private static final int colorDarkGray = FastColor.ARGB32.color(150, 105, 105, 105);
    private static final int colorWhite = FastColor.ARGB32.color(150, 255, 255, 255);
    private final Minecraft mc = Minecraft.getInstance();
    private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ShowKey.MODID, "key");
    public boolean onRender = false;
    private List<KeyMapping> modifierMappings = new ArrayList<>();
    private List<KeyMapping> displayKeyMappings = new ArrayList<>();
    private int showCount = 0;

    private void RenderAllKeys(GuiGraphics guiGraphics, float width, float height) {
        boolean br = ShowKeyConfig.displayMode == 1;
        boolean bb = ShowKeyConfig.displayMode == 0;
        int disc = ShowKeyConfig.displayCount;
        int hd = mc.font.lineHeight + 2;
        int displayCount = 0;
        int x = 1;
        int startX = (int) width - 1;
        if(br){
            x = startX;
            startX =1;
        }
        int startY = (int) height - mc.font.lineHeight - 2;
        int y = startY;
        int modifier = 0;
        int r = 10;

        if (ShowKeyConfig.displayMode == 0 && showCount != 0) {
            r = showCount / 2;
        }
        for (KeyMapping keyMapping : modifierMappings) {
            ShowKeyCondition condition = null;
            if (KeyInfoHelper.KEY_DISPLAY_RULE.containsKey(keyMapping.getName())) {
                condition = KeyInfoHelper.KEY_DISPLAY_RULE.get(keyMapping.getName());
            }
            if (keyMapping.getKeyModifier().isActive(keyMapping.getKeyConflictContext())) {
                if (condition == null || !condition.customPosition) {
                    if (displayCount > disc) {
                        continue;
                    }


                    renderKeyInfo(guiGraphics, keyMapping, x, y, true, bb && displayCount > r||br);
                    y -= hd;
                    if (y >= height || (bb && displayCount == r)) {
                        y = startY;
                        x = startX;
                    }
                    modifier++;
                    displayCount++;
                } else {
                    float size = (float) condition.size;
                    if (size <= 0) {
                        size = 1;
                    }
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(size, size, 1);
                    renderKeyInfo(guiGraphics, keyMapping, condition.coordinate.x, condition.coordinate.y, !condition.hideName, condition.drawRight);
                    guiGraphics.pose().popPose();
                }

            }
        }
        if (modifier == 0) {
            for (KeyMapping keyMapping : displayKeyMappings) {

                ShowKeyCondition condition = null;
                if (KeyInfoHelper.KEY_DISPLAY_RULE.containsKey(keyMapping.getName())) {
                    condition = KeyInfoHelper.KEY_DISPLAY_RULE.get(keyMapping.getName());
                }
                if (condition == null || !condition.customPosition) {
                    if (displayCount > disc) {
                        continue;
                    }
                    renderKeyInfo(guiGraphics, keyMapping, x, y, true, displayCount > r||br);
                    y -= hd;
                    if (y >= height || (bb && displayCount == r)) {
                        y = startY;
                        x = startX;
                    }
                    displayCount++;
                } else {
                    float size = (float) condition.size;
                    if (size <= 0) {
                        size = 1;
                    }
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(size, size, 1);
                    renderKeyInfo(guiGraphics, keyMapping, (int) (condition.coordinate.x / size), (int) (condition.coordinate.y / size), !condition.hideName, condition.drawRight);
                    guiGraphics.pose().popPose();
                }
            }
        }
        showCount = displayCount;
        if (showCount > disc) {
            showCount = disc;
        }
    }


    private void renderKeyInfo(GuiGraphics guiGraphics, KeyMapping keyMapping, int x, int y, boolean ShowString, boolean right) {

        try {
            String kId = keyMapping.getName();
            if (KeyGuiData.getKeyGuiData(kId) == null) {
                new KeyGuiData(keyMapping);
            }
            var data = KeyGuiData.getKeyGuiData(kId);
            if (data != null) {
                String key = data.getKey();
                int fw;
                int fh = y + mc.font.lineHeight + 1;
                if (right) {
                    fw = x;
                    x = x - mc.font.width(key) - 2;
                } else {
                    fw = x + mc.font.width(key) + 2;
                }
                int x1 = x + 1;
                int y1 = y + 1;
                boolean isKeyDown = keyMapping.isDown();
                int color0 = isKeyDown ? 0x696969 : 0xFFFFFF;
                int color1 = isKeyDown ? colorWhite : colorDarkGray;
                guiGraphics.fill(x, y, fw, fh, color1);
                guiGraphics.drawString(mc.font, key, x1, y1, color0);

                String keyName = data.getName();
                if (!ShowString) {
                    return;
                }
                int x2;
                if (right) {
                    x2 = x1 - mc.font.width(keyName) - 2;
                } else {
                    x2 = fw + 1;
                }
                StringWidget sw = new StringWidget(fw - x2, fh - y1, Component.literal(keyName), mc.font);
                guiGraphics.drawString(mc.font, keyName, x2, y1, color0);
            }
        } catch (Exception ignored) {

        }

    }

    public ResourceLocation id() {
        return this.id;
    }

    public void tick() {
        Set<KeyMapping> list = Arrays.stream(Minecraft.getInstance().options.keyMappings).filter(KeyInfoHelper::isShowKeyMapping).collect(Collectors.toSet());

        modifierMappings = list.stream()
                .filter(a1 -> a1.getKeyModifier() != KeyModifier.NONE).sorted(Comparator.comparingInt(a1 -> -a1.getKey().getValue())).toList();

        displayKeyMappings = list.stream()
                .filter(a -> a.getKeyModifier() == KeyModifier.NONE).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();

    }

    @Override
    @SuppressWarnings({"NullableProblems"})
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        onRender = true;
        guiGraphics.pose().pushPose();
        RenderSystem.enableBlend();
        float scaleNum = (float) ShowKeyConfig.UIScaleNumber;
        guiGraphics.pose().scale(scaleNum, scaleNum, 1);
        float height = guiGraphics.guiHeight();
        float width = guiGraphics.guiWidth();
        RenderAllKeys(guiGraphics, width / scaleNum, height / scaleNum);
        //guiGraphics.flush();
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();

    }

    private static class KeyGuiData {
        private static Map<String, KeyGuiData> AllKeyGuiData = new Hashtable<>();
        String ID;
        String key;
        String name;

        public KeyGuiData(KeyMapping keyMapping) {
            ID = keyMapping.getName();
            key = Component.keybind(keyMapping.getKey().getName()).getString().replaceFirst("key.keyboard.", "");
            if (key.length() <= 1) {
                key = key.toUpperCase();
            }
            name = Component.translatable(keyMapping.getName()).getString();
            if (AllKeyGuiData == null) {
                AllKeyGuiData = new Hashtable<>();
            }
            AllKeyGuiData.put(ID, this);
        }

        public static KeyGuiData getKeyGuiData(String ID) {
            if (AllKeyGuiData == null || AllKeyGuiData.isEmpty() || !AllKeyGuiData.containsKey(ID)) {
                return null;
            }
            return AllKeyGuiData.get(ID);
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }
    }
}
