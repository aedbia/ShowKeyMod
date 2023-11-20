package com.aedbia.showKey.client.gui;

import com.aedbia.showKey.ShowKey;
import com.aedbia.showKey.ShowKeyConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ShowKeyGui implements IGuiOverlay {

    @SuppressWarnings("FieldMayBeFinal")
    private static List<KeyMapping> keyMappings = new ArrayList<>();

    @SuppressWarnings("FieldMayBeFinal")
    private static List<KeyMapping> keyMappingsModifier = new ArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();
    private final String id;
    private int activeKeyCount = 10;

    public static boolean reDraw =true;
    private static final ResourceLocation button_down = new ResourceLocation(ShowKey.MODID,"textures/gui/button_down.png");
    private static final ResourceLocation button_release = new ResourceLocation(ShowKey.MODID,"textures/gui/button_release.png");

    public ShowKeyGui(){
        this.id = "keys";
    }

    @SuppressWarnings("unused")
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if(reDraw){
            keyMappings = Arrays.stream(mc.options.keyMappings).filter((a)->(!a.isUnbound()
                    &&a.getKeyModifier()==KeyModifier.NONE
                    &&KeyInfoHelper.isShowKeyMapping(a)
            )).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();
            keyMappingsModifier = Arrays.stream(mc.options.keyMappings).filter((a)->(!a.isUnbound()
                    &&a.getKeyModifier()!= KeyModifier.NONE
                    &&KeyInfoHelper.isShowKeyMapping(a)
            )).sorted(Comparator.comparingInt(a -> -a.getKey().getValue())).toList();
            reDraw=false;
            LogUtils.getLogger().debug("reDrawShowKeyOverLay");
        }
        double scale = ShowKeyConfig.UIScaleNumber;
        if(scale <0.1){
            scale = 0.5;
        }
        double x = 0;
        double wight = (double) screenWidth /8;
        double height = (double) screenHeight /(3* scale);
        int count = 0;
        int showCount = (int) (7/ scale);
        double y = (screenHeight/ scale - height/showCount);
        boolean NoModifierIsActive = true;
        boolean right = false;
        guiGraphics.pose().scale((float) scale, (float) scale, 1f);
        for (KeyMapping keyMapping : keyMappingsModifier) {
            boolean modifierIsDown = keyMapping.getKeyModifier().isActive(keyMapping.getKeyConflictContext());
            if(count>=2*showCount){
                break;
            }
            if(activeKeyCount/2 >= 1&&count==activeKeyCount/2){
                x = screenWidth/ scale -wight;
                y =(screenHeight/ scale - height/showCount);
                right = true;
            }
            if (modifierIsDown&&keyMapping.getKeyConflictContext().isActive()) {
                renderKeyInfo(guiGraphics,keyMapping, x, y, wight, height/showCount, right);
                y -= (height / showCount);
                NoModifierIsActive = false;
                count++;
            }
        }
        if(NoModifierIsActive){
            for (KeyMapping keyMapping : keyMappings) {
                if(count>=2*showCount){

                    break;
                }
                if(activeKeyCount/2 >= 1&&count==activeKeyCount/2){
                    x = screenWidth/ scale -wight;
                    y =(screenHeight/ scale - height/showCount);
                    right = true;
                }
                if(keyMapping.getKeyConflictContext().isActive()) {
                    renderKeyInfo(guiGraphics, keyMapping, x, y, wight, height / showCount, right);
                    y -= (height / showCount);
                    count++;
                }

            }
        }
        activeKeyCount = count;
        guiGraphics.pose().scale( (1/(float) scale),  (1/(float) scale), 1f);

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public void renderKeyInfo(GuiGraphics guiGraphics,KeyMapping keyMapping,double x, double y,double wight,double height,boolean right){
        String key = keyMapping.getKey().getDisplayName().getString().replaceFirst("key.keyboard.","");
        if(key.length()<=1){
            key = key.toUpperCase();
        }
        boolean isKeyDown = keyMapping.isDown();
        int color = isKeyDown?0x808080:0xFFFFFF;
        String keyName = Component.translatable(keyMapping.getName()).getString();
        int keyLoc = (int) (right?(x+wight*3/4):(x+wight/4));
        int texLoc = (int) (right?(x+wight/2):x);
        int keyNameLoc = (int)(right?(x-mc.font.width(keyName)+wight/2):(x+wight/2));
        guiGraphics.blit(isKeyDown?button_down:button_release,texLoc,(int) y,0,0,(int)(wight/2)-2,(int)height-2,(int)(wight/2)-2,(int)height-2);
        guiGraphics.drawCenteredString(mc.font, key, keyLoc, (int) y, color);
        guiGraphics.drawString(mc.font, keyName, keyNameLoc, (int) y, color);

    }
    public String id() {
        return this.id;
    }
}