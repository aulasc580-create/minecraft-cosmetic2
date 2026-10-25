package uz.cosmeticsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmeticsMod implements ClientModInitializer {
	public static final String MOD_ID = "cosmetics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyBinding openMenuKey;

	@Override
	public void onInitializeClient() {
		CosmeticRegistry.init();
		CosmeticsConfig.load();

		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.cosmetics.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.cosmetics"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenuKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new CosmeticsScreen(null));
				}
			}
		});

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, helper, context) -> {
			if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
				helper.register(new CosmeticFeatureRenderer(playerRenderer));
			}
		});

		LOGGER.info("Cosmetics mod yuklandi");
	}
}
