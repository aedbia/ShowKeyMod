package com.aedbia.showKey;

import com.aedbia.showKey.client.gui.KeyInfoHelper;
import com.aedbia.showKey.compatible.KeybindsGaloreCompatible;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ShowKey.MODID)
public class ShowKey
{
    public static final String MODID = "show_key";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();

    public ShowKey()
    {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShowKeyConfig.SPEC);
        modEventBus.register(new KeyInfoHelper());
        MinecraftForge.EVENT_BUS.addListener(KeybindsGaloreCompatible::onClientTick);
    }
    /*
     * private void commonSetup(final FMLCommonSetupEvent event) {
     * }
     */
}