package aedbia.showKey.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ShowKeyCondition {
    public static Map<String, ShowKeyCondition> ALL_RULE = new HashMap<>();
    private final Minecraft mc = Minecraft.getInstance();
    public List<Class<? extends Screen>> screens = new ArrayList<>();
    public List<String> handItems = new ArrayList<>();
    public List<String> OffHandItems = new ArrayList<>();
    public List<String> wearItem = new ArrayList<>();
    public List<Supplier<Boolean>> OtherConditions = new ArrayList<>();
    public List<String> riders = new ArrayList<>();
    public boolean NoRider = false;
    public boolean NullScreenShow = false;
    public boolean NoMainHainItem = false;
    public boolean NoOffHandItem = false;
    public boolean NoWearItem = false;

    private boolean autoCheck;

    public ShowKeyCondition(){
        autoCheck = true;
    }

    public ShowKeyCondition(boolean autoCheck){
        this.autoCheck = autoCheck;
    }

    public static void RecordCondition(String name) {
        if (ShowKeyCommandThread.stop) {
            if (!ShowKeyCondition.ALL_RULE.containsKey(name)) {
                ShowKeyCondition.ALL_RULE.put(name, new ShowKeyCondition());
            }
            if (ShowKeyCondition.ALL_RULE.containsKey(name)&&ShowKeyCondition.ALL_RULE.get(name).autoCheck) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null) {
                    ShowKeyCondition.ALL_RULE.get(name).NullScreenShow = true;
                } else {
                    ShowKeyCondition.ALL_RULE.get(name).screens.add(mc.screen.getClass());
                }
                if (mc.player != null) {
                    if (mc.player.getVehicle() == null) {
                        ShowKeyCondition.ALL_RULE.get(name).NoRider = true;
                    } else {
                        ShowKeyCondition.ALL_RULE.get(name).riders.add(mc.player.getVehicle().getType().getCategory().getName());
                    }
                    if (!ShowKeyCondition.ALL_RULE.get(name).NoMainHainItem) {
                        if (mc.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                            ShowKeyCondition.ALL_RULE.get(name).NoMainHainItem = true;
                        } else {

                            ShowKeyCondition.ALL_RULE.get(name).handItems.add(mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem().toString());
                        }
                    }
                    if (!ShowKeyCondition.ALL_RULE.get(name).NoOffHandItem) {
                        if (mc.player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                            ShowKeyCondition.ALL_RULE.get(name).NoOffHandItem = true;
                        } else {
                            ShowKeyCondition.ALL_RULE.get(name).OffHandItems.add(mc.player.getItemInHand(InteractionHand.OFF_HAND).getItem().toString());
                        }
                    }
                    if (!ShowKeyCondition.ALL_RULE.get(name).NoWearItem) {
                        List<String> a = new ArrayList<>();
                        for (ItemStack itemStack : mc.player.getArmorSlots()) {
                            if (!itemStack.isEmpty()) {
                                a.add(itemStack.getItem().toString());
                            }
                        }
                        if (a.isEmpty()) {
                            ShowKeyCondition.ALL_RULE.get(name).NoWearItem = true;
                        } else {
                            for (String itemName : a) {
                                ShowKeyCondition.ALL_RULE.get(name).wearItem.add(itemName);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean IsActive() {

        boolean a;
        if (mc.screen == null) {
            a = NullScreenShow;
        } else {
            a = !screens.isEmpty() && screens.contains(mc.screen.getClass());
        }
        if (!a) {
            return false;
        }
        boolean b = false;
        boolean c = false;
        boolean d = false;
        boolean e;
        if (mc.player != null) {
            if (NoMainHainItem) {
                b = true;
            } else if (!mc.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                String HandItemName = mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem().toString();
                b = handItems.contains(HandItemName);
            }
            if (!b) {
                return false;
            }
            if (NoOffHandItem) {
                c = true;
            } else if (!mc.player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                String HandItemName = mc.player.getItemInHand(InteractionHand.OFF_HAND).getItem().toString();
                c = OffHandItems.contains(HandItemName);
            }
            if (!c) {
                return false;
            }
            if (NoWearItem) {
                d = true;
            } else {
                for (ItemStack itemStack : mc.player.getArmorSlots()) {
                    if (!d && !itemStack.isEmpty() && wearItem.contains(itemStack.getItem().toString())) {
                        d = true;
                    }
                }
            }
            if (!d) {
                return false;
            }
            if (mc.player.getVehicle() == null) {
                e = NoRider;
            } else {
                e = !riders.isEmpty() && riders.contains(mc.player.getVehicle().getType().getCategory().getName());
            }
            if (!e) {
                return false;
            }
        }else {
            return false;
        }
        boolean f = false;
        if (OtherConditions.isEmpty()) {
            f = true;
        } else {
            for (Supplier<Boolean> v : OtherConditions) {
                if (!v.get()) {
                    f = false;
                    break;
                } else {
                    f = true;
                }
            }
        }
        //ShowKey.LOGGER.debug(Boolean.toString(e));
        return f;
    }
}
