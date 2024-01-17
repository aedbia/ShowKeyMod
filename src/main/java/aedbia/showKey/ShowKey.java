package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCommandThread;
import aedbia.showKey.configs.ShowKeyConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod(ShowKey.MODID)
public class ShowKey {
    public static final String MODID = "show_key";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Path CONFIG_PATCH = FMLPaths.CONFIGDIR.get().resolve(MODID);

    public ShowKey() {
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShowKeyConfig.SPEC);

    }

    public void onRegisterClientCommands(RegisterCommandsEvent event) {
        ShowKeyCommandThread.registerCommands(event);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ShowKeyConfig.initKeyConfig();
            KeyInfoHelper.start();
        }
    }
}