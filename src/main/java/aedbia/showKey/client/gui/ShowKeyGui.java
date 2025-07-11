package aedbia.showKey.client.gui;


import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.client.ShowKeyCondition;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.blaze3d.platform.InputConstants;
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

public class ShowKeyGui implements LayeredDraw.Layer {

    private static final int colorDarkGray = FastColor.ARGB32.color(150, 105, 105, 105);
    private static final int colorWhite = FastColor.ARGB32.color(150, 255, 255, 255);
    private final Minecraft mc = Minecraft.getInstance();
    private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ShowKey.MODID, "key");
    public boolean onRender = false;
    private KeyMapping[] displayKeyMappings = new KeyMapping[0];

    private void RenderAllKeys(GuiGraphics guiGraphics, float width, float height) {
        boolean br = ShowKeyConfig.displayMode == 1;
        boolean bb = ShowKeyConfig.displayMode == 0;
        int disc = Math.min(ShowKeyConfig.displayCount,displayKeyMappings.length);
        int hd = mc.font.lineHeight + 2;
        int displayCount = 0;
        int x = 1;
        int startX = (int) width - 1;
        if (br) {
            x = startX;
            startX = 1;
        }
        int startY = (int) height - mc.font.lineHeight - 2;
        int y = startY;
        int r;

        if (ShowKeyConfig.displayMode == 0) {
            r = disc / 2;
        }else {
            r = disc;
        }
        for (KeyMapping keyMapping : displayKeyMappings) {

            String ID = keyMapping.getName();
            ShowKeyCondition condition = null;
            if (KeyInfoHelper.KEY_DISPLAY_RULE.containsKey(ID)) {
                condition = KeyInfoHelper.KEY_DISPLAY_RULE.get(ID);
            }
            if (condition == null || !condition.customPosition) {
                if (displayCount > disc) {
                    continue;
                }
                renderKeyInfo(guiGraphics, keyMapping, x, y, true, displayCount > r || br);
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


    private void renderKeyInfo(GuiGraphics guiGraphics, KeyMapping keyMapping, int x, int y, boolean ShowString, boolean right) {
        try {
            String kId = keyMapping.getName();
            if (KeyGuiData.getKeyGuiData(kId) == null) {
                new KeyGuiData(keyMapping);
            }
            var data = KeyGuiData.getKeyGuiData(kId);
            if (data != null) {
                String key = data.getKeyName();
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
        displayKeyMappings = Arrays.stream(Minecraft.getInstance().options.keyMappings).filter(a -> {
            if (KeyInfoHelper.isShowKeyMapping(a)) {
                if (onRender) {
                    String id = a.getName();
                    if (KeyGuiData.getKeyGuiData(id) == null) {
                        new KeyGuiData(a);
                    }
                    KeyGuiData data = KeyGuiData.getKeyGuiData(id);
                    if (data != null) {
                        data.verifiedData();
                    }
                }
                return true;
            }
            return false;
        }).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toArray(KeyMapping[]::new);

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
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
        onRender = false;
    }

    private static class KeyGuiData {
        private static Map<String, KeyGuiData> AllKeyGuiData = new Hashtable<>();
        String ID;
        String keyName;
        String name;
        InputConstants.Key key;
        KeyModifier modifier;
        KeyMapping mapping;
        static List<InputConstants.Key> mouse = new ArrayList<>();
        public KeyGuiData(KeyMapping keyMapping) {
            ID = keyMapping.getName();
            this.mapping = keyMapping;
            this.modifier = keyMapping.getKeyModifier();
            this.key = keyMapping.getKey();
            initialData();
            AllKeyGuiData.put(ID, this);
        }

        private static String getKeyName(InputConstants.Key inputKey) {
            return inputKey.getDisplayName().getString();
        }

        public static KeyGuiData getKeyGuiData(String ID) {
            if (AllKeyGuiData == null || AllKeyGuiData.isEmpty() || !AllKeyGuiData.containsKey(ID)) {
                return null;
            }
            return AllKeyGuiData.get(ID);
        }

        private void initialData() {
            keyName = getKeyName(mapping.getKey());
            var modifier = mapping.getKeyModifier();
            if (modifier != KeyModifier.NONE) {
                var keys = modifier.codes();
                if (keys.length != 0) {
                    keyName = getKeyName(keys[0]) + "+" + keyName;
                }
            }
            name = Component.translatable(mapping.getName()).getString();
            if (AllKeyGuiData == null) {
                AllKeyGuiData = new Hashtable<>();
            }
        }

        public void verifiedData() {
            if (change()) {
                initialData();
            }
        }

        protected boolean change() {
            return mapping.getKeyModifier() != modifier ||
                    mapping.getKey() != key;
        }

        public String getName() {
            return name;
        }

        public String getKeyName() {
            return keyName;
        }
    }
}
