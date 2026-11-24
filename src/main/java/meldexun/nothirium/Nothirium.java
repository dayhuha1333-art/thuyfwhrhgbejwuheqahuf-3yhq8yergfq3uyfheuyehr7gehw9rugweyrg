package meldexun.nothirium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = "nothirium", name = "Nothirium", version = "2.1.4", clientSideOnly = true)
public class Nothirium {
    public static boolean xrayEnabled = false;
    public static final Map<String, Integer> blockColors = new HashMap<>();
    private static KeyBinding toggleKey;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();
        xrayEnabled = cfg.getBoolean("enableOcclusionCulling", "performance", false, "");
        blockColors.put("minecraft:diamond_ore", 0x00FFFF);
        blockColors.put("minecraft:emerald_ore", 0x00FF00);
        blockColors.put("minecraft:gold_ore", 0xFFD700);
        blockColors.put("minecraft:iron_ore", 0xC0C0C0);
        blockColors.put("minecraft:coal_ore", 0x333333);
        blockColors.put("minecraft:redstone_ore", 0xFF0000);
        blockColors.put("minecraft:lapis_ore", 0x0000FF);
        blockColors.put("minecraft:quartz_ore", 0xFFF5EE);
        cfg.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        toggleKey = new KeyBinding("key.nothirium.profile", Keyboard.KEY_X, "key.categories.nothirium");
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if (Minecraft.getMinecraft().currentScreen != null) return;
        if (toggleKey.isPressed()) {
            xrayEnabled = !xrayEnabled;
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
                "Nothirium: Render Profile " + (xrayEnabled ? "ULTRA" : "VANILLA")
            ));
        }
    }
}
