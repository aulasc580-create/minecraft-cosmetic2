package uz.cosmeticsmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sozlamalar: .minecraft/config/cosmetics.json */
public final class CosmeticsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static CosmeticsConfig instance = new CosmeticsConfig();

	/** slot kaliti -> cosmetic id (masalan "back" -> "backpack") */
	public Map<String, String> equipped = new LinkedHashMap<>();
	/** Hamma cosmetic'lar o'lchamini kattalashtirish/kichraytirish (1.0 = odatiy) */
	public float sizeMultiplier = 1.0f;

	public static CosmeticsConfig get() {
		return instance;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("cosmetics.json");
	}

	public static void load() {
		Path p = path();
		if (Files.exists(p)) {
			try (Reader r = Files.newBufferedReader(p)) {
				CosmeticsConfig loaded = GSON.fromJson(r, CosmeticsConfig.class);
				if (loaded != null) {
					if (loaded.equipped == null) loaded.equipped = new LinkedHashMap<>();
					instance = loaded;
					return;
				}
			} catch (Exception e) {
				CosmeticsMod.LOGGER.warn("Config o'qilmadi, standart ishlatiladi", e);
			}
		}
		// Birinchi ishga tushirish: backpack avtomatik kiyiladi
		instance = new CosmeticsConfig();
		instance.equipped.put(CosmeticSlot.BACK.key, "backpack");
		save();
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(path())) {
			GSON.toJson(instance, w);
		} catch (Exception e) {
			CosmeticsMod.LOGGER.warn("Config saqlanmadi", e);
		}
	}

	public Cosmetic equippedIn(CosmeticSlot slot) {
		return CosmeticRegistry.get(equipped.get(slot.key));
	}

	public void equip(CosmeticSlot slot, Cosmetic c) {
		if (c == null) equipped.remove(slot.key);
		else equipped.put(slot.key, c.id);
	}

	public List<Cosmetic> allEquipped() {
		List<Cosmetic> out = new ArrayList<>();
		for (CosmeticSlot s : CosmeticSlot.values()) {
			Cosmetic c = equippedIn(s);
			if (c != null) out.add(c);
		}
		return out;
	}
}
