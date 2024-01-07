package aedbia.showKey.client.gui;

import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import aedbia.showKey.ShowKeyConfig;
import aedbia.showKey.compatible.keybindsGalore.KeybindsGaloreCompatible;
import aedbia.showKey.mixins.SKMixins;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowKeyGui implements IIngameOverlay {

    private static final ResourceLocation button_down = new ResourceLocation(ShowKey.MODID, "textures/gui/button_down.png");
    private static final ResourceLocation button_release = new ResourceLocation(ShowKey.MODID, "textures/gui/button_release.png");
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);
    private final Minecraft mc = Minecraft.getInstance();
    private final String id;
    private List<KeyMapping> displayKeyMappings = new ArrayList<>();
    private List<KeyMapping> modifierMappings = new ArrayList<>();
    private int activeKeyCount = 10;

    public ShowKeyGui() {
        this.id = "keys";
        scheduled.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unused")
    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        //LogUtils.getLogger().debug("1111adada");
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
        poseStack.scale((float) scale, (float) scale, 1f);
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
                renderKeyInfo(poseStack, keyMapping, x, y, wight, height / showCount, right);
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
        boolean isKeyDown = ((SKMixins.AccessorKeyMapping) keyMapping).getIsDown();
        int color = isKeyDown ? 0x808080 : 0xFFFFFF;
        String keyName = new TranslatableComponent(keyMapping.getName()).getString();
        int keyLoc = (int) (right ? (x + wight * 3 / 4) : (x + wight / 4));
        int texLoc = (int) (right ? (x + wight / 2) : x);
        int keyNameLoc = (int) (right ? (x - mc.font.width(keyName) + wight / 2) : (x + wight / 2));
        ResourceLocation resourceLocation = isKeyDown ? button_down : button_release;
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, texLoc, (int) y, 0, 0, (int) (wight / 2) - 2, (int) height - 2, (int) (wight / 2) - 2, (int) height - 2);
        GuiComponent.drawCenteredString(poseStack, mc.font, key, keyLoc, (int) y, color);
        GuiComponent.drawString(poseStack, mc.font, keyName, keyNameLoc, (int) y, color);

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