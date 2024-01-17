package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCommandThread;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(ShowKey.MODID)
public class ShowKey {
    public static final String MODID = "show_key";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Path CONFIG_PATCH = FMLPaths.CONFIGDIR.get().resolve(MODID);

    public ShowKey(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        bus.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShowKeyConfig.SPEC);
    }

    @SubscribeEvent
    public void onRenderBar(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), KeyInfoHelper.gui.id(), KeyInfoHelper.gui);
    }

    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
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