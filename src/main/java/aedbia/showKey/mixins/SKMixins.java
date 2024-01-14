package aedbia.showKey.mixins;

import aedbia.showKey.client.ShowKeyCondition;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SKMixins {
    @Mixin({KeyMapping.class})
    public interface AccessorKeyMapping {
        @Accessor("isDown")
        boolean getIsDown();
    }

    @Mixin(KeyMapping.class)
    public abstract static class MixinKeyMapping {
        @Final
        @Shadow
        private String name;

        @Inject(method = "consumeClick", at = @At("HEAD"))
        private void preClick(CallbackInfoReturnable<Boolean> cir) {
            ShowKeyCondition.RecordCondition(name);
        }

        @Inject(method = "isDown", at = @At("HEAD"))
        private void preDown(CallbackInfoReturnable<Boolean> cir) {
            ShowKeyCondition.RecordCondition(name);
        }
    }
}