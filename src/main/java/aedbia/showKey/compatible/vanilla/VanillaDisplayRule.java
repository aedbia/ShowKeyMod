package aedbia.showKey.compatible.vanilla;

import aedbia.showKey.API.SKRuleRegisterPlugin;
import aedbia.showKey.API.ShowKeyPlugin;
import aedbia.showKey.KeyInfoHelper;
import aedbia.showKey.ShowKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;

@ShowKeyPlugin(ShowKey.MODID)
public class VanillaDisplayRule implements SKRuleRegisterPlugin {

    @Override
    public void registerRule() {
        Minecraft mc = Minecraft.getInstance();
        Options options = mc.options;
        KeyInfoHelper.registerDisplayRule(options.keyChat,a->mc.screen == null||mc.screen.getClass()!= ChatScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keyCommand,a->mc.screen == null||mc.screen.getClass()!= ChatScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keyInventory,a->mc.screen == null||mc.screen.getClass() == CreativeModeInventoryScreen.class||mc.screen.getClass() == InventoryScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keyAdvancements,a->mc.screen == null||mc.screen.getClass() == AdvancementsScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keySocialInteractions,a->mc.screen == null||mc.screen.getClass() == SocialInteractionsScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keyJump,a->mc.screen == null||mc.screen.getClass()!= ChatScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keySprint, a->mc.screen == null||mc.screen.getClass()!= ChatScreen.class);
        KeyInfoHelper.registerDisplayRule(options.keyShift,a->mc.screen == null||mc.screen.getClass()!= ChatScreen.class);
    }
}