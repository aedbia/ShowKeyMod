package aedbia.showKey.client.gui;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.compatible.keybindsGalore.KeybindsGaloreCompatible;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowKeyGui implements IGuiOverlay {

    private static final ResourceLocation button_down = new ResourceLocation(ShowKey.MODID, "textures/gui/button_down.png");
    private static final ResourceLocation button_release = new ResourceLocation(ShowKey.MODID, "textures/gui/button_release.png");
    private final Minecraft mc = Minecraft.getInstance();
    private final String id;
    public boolean onRender = false;
    private List<KeyMapping> displayKeyMappings = new ArrayList<>();
    private List<KeyMapping> modifierMappings = new ArrayList<>();
    private int activeKeyCount = 10;

    private int showCount = 7;

    public ShowKeyGui() {
        this.id = "keys";
    }

    @SuppressWarnings("unused")
    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        onRender = true;
        double scale = ShowKeyConfig.UIScaleNumber;
        int mode = ShowKeyConfig.displayMode;
        if (scale < 0.1) {
            scale = 0.5;
        }
        double x = 0;
        double wight = (double) screenWidth / 8;
        double height = (double) screenHeight / (3 * scale);
        int count = 0;
        double y = (screenHeight / scale - height / showCount);
        boolean right = false;
        poseStack.scale((float) scale, (float) scale, 1f);
        for (KeyMapping keyMapping : modifierMappings) {
            if (count >= 2 * showCount || (mode != 0 && count >= showCount)) {
                break;
            }
            if ((mode == 0 && activeKeyCount / 2 >= 1 && count == activeKeyCount / 2) || (mode == 1 && count == 0)) {
                x = screenWidth / scale - wight;
                y = (screenHeight / scale - height / showCount);
                right = true;
            }
            if (keyMapping.getKeyConflictContext().isActive() && keyMapping.getKeyModifier().isActive(keyMapping.getKeyConflictContext())) {
                renderKeyInfo(poseStack, keyMapping, x, y, wight, height / showCount, right);
                y -= (height / showCount);
                count++;
            }
        }
        if (count == 0) {
            for (KeyMapping keyMapping : displayKeyMappings) {
                if (count >= 2 * showCount || (mode != 0 && count >= showCount)) {
                    break;
                }
                if ((mode == 0 && activeKeyCount / 2 >= 1 && count == activeKeyCount / 2) || (mode == 1 && count == 0)) {
                    x = screenWidth / scale - wight;
                    y = (screenHeight / scale - height / showCount);
                    right = true;
                }
                if (keyMapping.getKeyConflictContext().isActive()) {
                    renderKeyInfo(poseStack, keyMapping, x, y, wight, height / showCount, right);
                    y -= (height / showCount);
                    count++;
                }
            }
        }
        activeKeyCount = count;
        poseStack.scale((1 / (float) scale), (1 / (float) scale), 1f);
    }

    public void renderKeyInfo(PoseStack poseStack, KeyMapping keyMapping, double x, double y, double wight, double height, boolean right) {
        String key = keyMapping.getKey().getDisplayName().getString().replaceFirst("key.keyboard.", "");
        if (key.length() <= 1) {
            key = key.toUpperCase();
        }
        boolean isKeyDown = keyMapping.isDown();
        int color = isKeyDown ? 0x808080 : 0xFFFFFF;
        String keyName = Component.translatable(keyMapping.getName()).getString();
        int keyLoc = (int) (right ? (x + wight * 3 / 4) : (x + wight / 4));
        int texLoc = (int) (right ? (x + wight / 2) : x);
        int keyNameLoc = (int) (right ? (x - mc.font.width(keyName) + wight / 2) : (x + wight / 2));
        ResourceLocation resourceLocation = isKeyDown ? button_down : button_release;
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, texLoc, (int) y, 0, 0, (int) (wight / 2) - 2, (int) height - 2, (int) (wight / 2) - 2, (int) height - 2);
        GuiComponent.drawCenteredString(poseStack, mc.font, key, keyLoc, (int) y, color);
        GuiComponent.drawString(poseStack, mc.font, keyName, keyNameLoc, (int) y, color);

    }

    public String id() {
        return this.id;
    }

    public void tick() {
        double scale = ShowKeyConfig.UIScaleNumber;
        showCount = (int) (7 / scale);
        KeybindsGaloreCompatible.receiveIMCMessage();
        List<KeyMapping> list = Arrays.stream(mc.options.keyMappings).filter(KeyInfoHelper::isShowKeyMapping).collect(Collectors.toMap(KeyMapping::getKey, Function.identity(), (a, b) -> a)).values().stream().toList();
        List<KeyMapping> modifier = list.stream()
                .filter(a -> a.getKeyModifier() != KeyModifier.NONE).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();
        if (modifier.size() > showCount) {
            modifierMappings = modifier.subList(modifier.size() - showCount, modifier.size());
        } else {
            modifierMappings = modifier;
        }
        List<KeyMapping> noModifier = list.stream()
                .filter(a -> a.getKeyModifier() == KeyModifier.NONE).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();
        if (noModifier.size() > showCount) {
            displayKeyMappings = noModifier.subList(noModifier.size() - showCount, noModifier.size());
        } else {
            displayKeyMappings = noModifier;
        }
    }
}