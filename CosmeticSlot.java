package uz.cosmeticsmod;

import net.minecraft.text.Text;

/** Har bir slotga bir vaqtda faqat bitta cosmetic kiyiladi. */
public enum CosmeticSlot {
	BACK("back"),
	HEAD("head"),
	CAPE("cape"),
	WINGS("wings");

	public final String key;

	CosmeticSlot(String key) {
		this.key = key;
	}

	public Text displayName() {
		return Text.translatable("slot.cosmetics." + key);
	}

	public static CosmeticSlot byKey(String key) {
		for (CosmeticSlot s : values()) {
			if (s.key.equals(key)) return s;
		}
		return null;
	}
}
