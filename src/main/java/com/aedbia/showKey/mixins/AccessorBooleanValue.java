package com.aedbia.showKey.mixins;

import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(ForgeConfigSpec.ConfigValue.class)
public interface AccessorBooleanValue {
    @Accessor("spec") void setSpec(ForgeConfigSpec spec);
}