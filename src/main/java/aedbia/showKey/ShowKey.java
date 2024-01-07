package aedbia.showKey;

import aedbia.showKey.client.ShowKeyCommandThread;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ShowKey.MODID)
public class ShowKey {
    public static final String MODID = "show_key";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();

    public ShowKey() {
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
        MinecraftForge.EVENT_BUS.register(this);
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
}