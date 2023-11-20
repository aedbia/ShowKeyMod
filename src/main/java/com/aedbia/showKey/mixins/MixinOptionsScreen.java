package com.aedbia.showKey.mixins;

import com.aedbia.showKey.client.gui.ShowKeyGui;
import net.minecraft.client.gui.screens.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {
    @Inject(method = {"onClose"},at= {@At("RETURN")})
    public void onClose(CallbackInfo ci){
        ShowKeyGui.reDraw = true;
    }
}