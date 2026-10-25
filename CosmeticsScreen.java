package uz.cosmeticsmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Cosmetic tanlash menyusi (K tugmasi). Har slot uchun tugma: bosganda keyingi variantga o'tadi. */
public class CosmeticsScreen extends Screen {
	private final Screen parent;

	public CosmeticsScreen(Screen parent) {
		super(Text.translatable("screen.cosmetics.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int y = this.height / 4;
		for (CosmeticSlot slot : CosmeticSlot.values()) {
			if (CosmeticRegistry.forSlot(slot).isEmpty()) continue;
			ButtonWidget button = ButtonWidget.builder(label(slot), btn -> {
				cycle(slot);
				btn.setMessage(label(slot));
			}).dimensions(this.width / 2 - 100, y, 200, 20).build();
			this.addDrawableChild(button);
			y += 24;
		}
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
				.dimensions(this.width / 2 - 100, y + 12, 200, 20).build());
	}

	private static Text label(CosmeticSlot slot) {
		Cosmetic c = CosmeticsConfig.get().equippedIn(slot);
		Text value = c == null ? Text.translatable("cosmetics.none") : c.displayName();
		return Text.empty().append(slot.displayName()).append(": ").append(value);
	}

	private static void cycle(CosmeticSlot slot) {
		List<Cosmetic> options = new ArrayList<>();
		options.add(null); // "None"
		options.addAll(CosmeticRegistry.forSlot(slot));
		Cosmetic current = CosmeticsConfig.get().equippedIn(slot);
		int next = (options.indexOf(current) + 1) % options.size();
		CosmeticsConfig.get().equip(slot, options.get(next));
		CosmeticsConfig.save();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
	}

	@Override
	public void close() {
		CosmeticsConfig.save();
		this.client.setScreen(parent);
	}
}
