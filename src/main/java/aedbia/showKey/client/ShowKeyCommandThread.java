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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
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

    public static void listHoldItem(InteractionHand hand) {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            String item;
            List<String> tags = new ArrayList<>();
            String title;
            ItemStack stack=player.getItemInHand(hand);
            if(hand == InteractionHand.MAIN_HAND){
                title = "item.modifiers.offhand";
            }else {
                title = "item.modifiers.mainhand";
            }
            if (stack.isEmpty()) {
                item = "null";
            } else {
                item = stack.getDescriptionId();
                for(var t : stack.getTags().toList()){
                    tags.add(t.toString());
                }
            }
            Minecraft.getInstance().gui.getChat().addMessage(getCopyItemWithTags(title, item,tags));

        }
    }

    public static void listEquipItem(int index) {
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
                if(b == index) {
                    String x;
                    List<String> tags = new ArrayList<>();
                    if (stack.isEmpty()) {
                        x = "null";
                    } else {
                        x = stack.getDescriptionId();
                        for(var t : stack.getTags().toList()){
                            tags.add(t.toString());
                        }
                    }

                    component = getCopyItemWithTags(a[b], x,tags);
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
            List<String> tags = new ArrayList<>();
            if (player.getVehicle() != null) {
                ride = player.getVehicle().getType().toString();
                tags.addAll(player.getVehicle().getTags());
            } else {
                ride = "null";
            }
            String a = Component.translatable("commands.ride.already_riding").getString().replace("%s", "") + ":";
            Minecraft.getInstance().gui.getChat().addMessage(getCopyItemWithTags(a, ride,tags));
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
    private static  MutableComponent getCopyItemWithTags(String title,String copyString,List<String> tags){
        var c = getCopyComponent(title,copyString);
        if(tags!=null&& !tags.isEmpty()){
            c.append("\nTags:");
            for (String str:tags){
                c.append("\n").append(getCopyComponent("",str));
            }
        }
        return c;
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
                                Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_keys_start"));
                                thread.start();
                            } else {
                                if (!thread.getAllScreen) {
                                    thread.stop = true;
                                    thread = null;
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_keys_stop"));
                                } else {
                                    thread.getAllScreen = false;
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_screen_stop"));
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_keys_start"));
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
                                Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_screen_start"));
                                thread.start();
                            } else {
                                if (thread.getAllScreen) {
                                    thread.stop = true;
                                    thread = null;
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_screen_stop"));
                                } else {
                                    thread.getAllScreen = true;
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_keys_stop"));
                                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("monitor_screen_start"));
                                }
                            }
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("item")
                        .then(Commands.literal("main_hand").executes(a -> {
                            ShowKeyCommandThread.listHoldItem(InteractionHand.MAIN_HAND);
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("item")
                        .then(Commands.literal("off_hand").executes(a -> {
                            ShowKeyCommandThread.listHoldItem(InteractionHand.OFF_HAND);
                            return 1;
                        }))));
        event.getDispatcher()
                .register(commands.then(Commands.literal("vehicle")
                        .then(Commands.literal("ride").executes(a -> {
                            ShowKeyCommandThread.listRideVehicle();
                            return 1;
                        }))));
        for (int i = 0;i<4;i++){
            int finalI = i;
            event.getDispatcher()
                    .register(commands.then(Commands.literal("equip")
                            .then(Commands.literal(Integer.toString(finalI)).executes(a -> {
                                ShowKeyCommandThread.listEquipItem(finalI);
                                return 1;
                            }))));
        }


    }

    @Override
    public void run() {
        LOGGER.debug("ShowKeyCommandThread" + " start!");
        while (!stop && Minecraft.getInstance().isRunning()) {
            //this.wait(1);
            if (!getAllScreen) {
                for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                    if (keyMapping.isDown()) {
                        if (!this.keyMapping.contains(keyMapping) && Minecraft.getInstance().player != null) {
                            this.keyMapping.add(keyMapping);
                            Minecraft.getInstance().gui.getChat().addMessage(getCopyComponent("", keyMapping.getName()));
                        }
                    } else this.keyMapping.remove(keyMapping);
                }
            } else {
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
                        listHoldItem(InteractionHand.MAIN_HAND);
                        listHoldItem(InteractionHand.OFF_HAND);
                        for (int i = 0;i<4;i++){
                            listEquipItem(i);
                        }

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
