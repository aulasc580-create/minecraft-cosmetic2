package uz.cosmeticsmod;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CosmeticRegistry {
	private static final Map<String, Cosmetic> ALL = new LinkedHashMap<>();

	private CosmeticRegistry() {}

	public static void init() {
		// ---- BACKPACK (Gucci) ----
		// Model OBJ koordinatalarida: yuqori y=19.34, ichki (orqaga tegadigan) yuz z=-1.5, markaz x=-7.96.
		// Model X o'qi atrofida 180 gradus buriladi (OBJ: y yuqoriga, tashqi tomon -z;
		// o'yinchi modelida: y pastga, orqa tomon +z).
		register(new Cosmetic(
				"backpack", CosmeticSlot.BACK,
				"/assets/cosmetics/models/backpack.quads",
				Identifier.of(CosmeticsMod.MOD_ID, "textures/cosmetic/backpack.png"),
				model -> model.body,
				-7.96f, 19.34f, -1.5f,   // pivot (model ichida)
				0f, 0.5f, 2.6f,          // joylashuv: tana markazidan (x, y, z piksel)
				0.5f,                    // o'lcham (1.0 = original, juda katta bo'ladi)
				180f));                  // X o'qi atrofida burish
	}

	public static void register(Cosmetic c) {
		ALL.put(c.id, c);
	}

	public static Cosmetic get(String id) {
		return id == null ? null : ALL.get(id);
	}

	public static List<Cosmetic> forSlot(CosmeticSlot slot) {
		List<Cosmetic> out = new ArrayList<>();
		for (Cosmetic c : ALL.values()) {
			if (c.slot == slot) out.add(c);
		}
		return out;
	}
}
