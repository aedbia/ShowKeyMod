package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCommandThread;
import aedbia.showKey.client.gui.ShowKeyGui;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ShowKey.MODID)
public class ShowKey {
    public static final String MODID = "show_key";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();

    public ShowKey(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        //NeoForge.EVENT_BUS.register(this);
        bus.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShowKeyConfig.SPEC);
    }

    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal(MODID).requires(a -> a.hasPermission(2));
        event.getDispatcher()
                .register(commands.then(Commands.literal("monitor")
                        .then(Commands.literal("start").executes(a -> {
                            new ShowKeyCommandThread();
                            ShowKeyCommandThread.stop = false;
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("monitor")
                        .then(Commands.literal("stop").executes(a -> {
                            ShowKeyCommandThread.stop = true;
                            return 1;
                        }))));
    }

    @SubscribeEvent
    public void onRenderBar(RegisterGuiOverlaysEvent event) {
        ShowKeyGui gui = new ShowKeyGui();
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), gui.id(), gui);
    }
}