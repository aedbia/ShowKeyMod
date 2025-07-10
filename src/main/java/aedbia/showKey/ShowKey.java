package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCommandThread;
import aedbia.showKey.screen.ShowkeyConfigScreen;
import aedbia.showKey.configs.ShowKeyConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(value = ShowKey.MODID, dist = Dist.CLIENT)
public class ShowKey {
    public static final String MODID = "show_key";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static ModContainer showKey = null;

    public ShowKey(IEventBus modEventBus, ModContainer modContainer) {
        showKey = modContainer;
        NeoForge.EVENT_BUS.register(this);
        modEventBus.register(new KeyInfoHelper());
        modContainer.registerConfig(ModConfig.Type.CLIENT, ShowKeyConfig.SPEC);
        //modContainer.registerExtensionPoint(IConfigScreenFactory.class, TestScreen::new);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ShowkeyConfigScreen::new);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ShowKeyCommandThread.registerCommands(event);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            if (showKey != null) {
                ShowKeyConfig.RegisterConfig(showKey);
                //ShowKeyConfig.initKeyConfig();
                ShowKeyConfig.load();
                ShowKeyConfig.canLoad = true;
            }
            KeyInfoHelper.start();
        }
    }
}