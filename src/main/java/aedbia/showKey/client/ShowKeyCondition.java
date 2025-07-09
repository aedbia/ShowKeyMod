package aedbia.showKey.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ShowKeyCondition {

    public boolean hide = false;
    public boolean customPosition = false;
    public Point coordinate;
    public boolean conditionDisplay = false;
    public List<String> boundMainHandItem = new ArrayList<>();
    public List<String> boundOffHandItem = new ArrayList<>();
    public List<String> boundEquipment = new ArrayList<>();
    public List<String> boundVehicle = new ArrayList<>();
    public List<String> boundScreens = new ArrayList<>();
    public List<Supplier<Boolean>> OtherConditions = new ArrayList<>();
    public boolean hideName = false;
    public boolean drawRight = false;
    public double size = 1.0f;

    public boolean isActive() {
        if (hide) {
            return false;
        }
        if (!conditionDisplay) {
            return true;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean a;
        if (boundScreens.isEmpty()) {
            a = true;
        } else {
            if (mc.screen == null) {
                a = boundScreens.contains("null");
            } else {
                a = boundScreens.contains(mc.screen.getClass().getName());
            }
        }
        if (!a) {
            return false;
        }
        if (mc.player != null) {

            LocalPlayer player = mc.player;
            boolean b;
            if (boundMainHandItem.isEmpty()) {
                b = true;
            } else {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.isEmpty()) {
                    b = boundMainHandItem.contains("null");
                } else {
                    b = boundMainHandItem.contains(stack.getDescriptionId());
                }
            }
            if (!b) {
                return false;
            }
            boolean c;
            if (boundOffHandItem.isEmpty()) {
                c = true;
            } else {
                ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
                if (stack.isEmpty()) {
                    c = boundOffHandItem.contains("null");
                } else {
                    c = boundOffHandItem.contains(stack.getDescriptionId());
                }
            }
            if (!c) {
                return false;
            }
            boolean d;
            if (boundEquipment.isEmpty()) {
                d = true;
            } else {
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : player.getArmorSlots()) {
                    if (!stack.isEmpty()) {
                        stacks.add(stack);
                    }
                }
                if (stacks.isEmpty()) {
                    d = boundEquipment.contains("null");
                } else {
                    d = stacks.stream().anyMatch(o -> boundEquipment.contains(o.getDescriptionId()));
                }
            }
            if (!d) {
                return false;
            }
            boolean e;
            if (boundVehicle.isEmpty()) {
                e = true;
            } else {
                Entity vehicle = player.getVehicle();
                if (vehicle == null) {
                    e = boundVehicle.contains("null");
                } else {
                    e = boundVehicle.contains(vehicle.getType().toString());
                }
            }
            if (!e) {
                return false;
            }
            boolean f;
            if (OtherConditions.isEmpty()) {
                f = true;
            } else {
                f = OtherConditions.stream().allMatch(Supplier::get);
            }
            return f;
        }
        return false;
    }

//    private boolean empty(List<String> list) {
//        List<String> list1 = list.stream().filter(a -> !a.contains("example")).toList();
//        return list1.isEmpty();
//    }
}