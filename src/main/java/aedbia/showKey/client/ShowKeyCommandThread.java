package aedbia.showKey.client;

import aedbia.showKey.ShowKey;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowKeyCommandThread extends Thread {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ShowKeyCommandThread thread = null;
    private boolean stop = true;
    private boolean getAllScreen = false;
    @SuppressWarnings("FieldMayBeFinal")
    private List<KeyMapping> keyMapping = new ArrayList<>();
    private String screenTitle = "";

    public ShowKeyCommandThread() {
        super("ShowKeyCommandThread");
    }

    public static void listHoldItem() {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            String mainItem;
            if (player.getMainHandItem().isEmpty()) {
                mainItem = "null";
            } else {
                mainItem = player.getMainHandItem().getDescriptionId();
            }
            String offItem;
            if (player.getOffhandItem().isEmpty()) {
                offItem = "null";
            } else {
                offItem = player.getOffhandItem().getItem().getDescriptionId();
            }
            Minecraft.getInstance().gui.getChat().addMessage(getCopyComponent("item.modifiers.mainhand", mainItem).append(CommonComponents.NEW_LINE).append(getCopyComponent("item.modifiers.offhand", offItem)));

        }
    }

    public static void listEquipItem() {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            String[] a = new String[]{
                    "item.modifiers.head",
                    "item.modifiers.chest",
                    "item.modifiers.legs",
                    "item.modifiers.feet"
            };
            int b = 0;
            MutableComponent component = null;
            for (ItemStack stack : player.getArmorSlots()) {
                String x;
                if (stack.isEmpty()) {
                    x = "null";
                } else {
                    x = stack.getDescriptionId();
                }
                if (component == null) {
                    component = getCopyComponent(a[b], x);
                } else {
                    component.append(CommonComponents.NEW_LINE).append(getCopyComponent(a[b], x));
                }
                b++;
                if (b >= a.length) {
                    b = a.length - 1;
                }
            }
            if (component != null) {
                Minecraft.getInstance().gui.getChat().addMessage(component);
            }

        }
    }

    public static void listRideVehicle() {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            String ride;
            if (player.getVehicle() != null) {
                ride = player.getVehicle().getType().toString();
            } else {
                ride = "null";
            }
            String a = Component.translatable("show_key.Now.riding").getString().replace("%s", "");
            Minecraft.getInstance().gui.getChat().addMessage(getCopyComponent(a, ride));
        }
    }

    private static MutableComponent getCopyComponent(String string, String copyString) {
        MutableComponent component = Component.literal(copyString).withStyle(Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyString))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tips.show_key.click_to_copy"))));
        if (!string.isEmpty()) {
            return Component.translatable(string).append(" ").append(component);
        } else {
            return component;
        }
    }

    public static void registerCommands(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal(ShowKey.MODID).requires(a -> a.hasPermission(2));
        event.getDispatcher()
                .register(commands.then(Commands.literal("monitor")
                        .then(Commands.literal("keymapping").executes(a -> {
                            if (thread == null) {
                                thread = new ShowKeyCommandThread();
                                thread.stop = false;
                                thread.getAllScreen = false;
                                thread.start();
                            }else {
                                if(!thread.getAllScreen){
                                    thread.stop = true;
                                    thread = null;
                                }else {
                                    thread.getAllScreen = false;
                                }
                            }
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("monitor")
                        .then(Commands.literal("screen").executes(a -> {
                            if (thread == null) {
                                thread = new ShowKeyCommandThread();
                                thread.stop = false;
                                thread.getAllScreen = true;
                                thread.start();
                            }else {
                                if(thread.getAllScreen){
                                    thread.stop = true;
                                    thread = null;
                                }else {
                                    thread.getAllScreen = true;
                                }
                            }
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("item")
                        .then(Commands.literal("hold").executes(a -> {
                            ShowKeyCommandThread.listHoldItem();
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("vehicle")
                        .then(Commands.literal("ride").executes(a -> {
                            ShowKeyCommandThread.listRideVehicle();
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("equip")
                        .then(Commands.literal("list").executes(a -> {
                            ShowKeyCommandThread.listEquipItem();
                            return 1;
                        }))));
    }

    @Override
    public void run() {
        LOGGER.debug("ShowKeyCommandThread" + " start!");
        while (!stop && Minecraft.getInstance().isRunning()) {
            //this.wait(1);
            if(!getAllScreen){
            for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                if (keyMapping.isDown()) {
                    if (!this.keyMapping.contains(keyMapping) && Minecraft.getInstance().player != null) {
                        this.keyMapping.add(keyMapping);
                        Minecraft.getInstance().gui.getChat().addMessage(getCopyComponent("", keyMapping.getName()));
                    }
                } else this.keyMapping.remove(keyMapping);
            }
            }else {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345)) {
                    String a;
                    if (Minecraft.getInstance().screen == null) {
                        a = "null";
                    } else {
                        a = Minecraft.getInstance().screen.getClass().getName();
                    }
                    if (!Objects.equals(screenTitle, a)) {
                        this.screenTitle = a;
                        Minecraft.getInstance().gui.getChat().addMessage(getCopyComponent("Show_key.Now.Screen", a));
                        listHoldItem();
                        listEquipItem();
                        listRideVehicle();
                    }
                } else {
                    screenTitle = "";
                }
            }
        }
        LOGGER.debug("ShowKeyCommandThread" + " stop!");
    }
}
