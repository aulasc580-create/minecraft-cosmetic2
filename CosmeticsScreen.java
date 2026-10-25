package uz.cosmeticsmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Cosmetic tanlash menyusi (K tugmasi).
 * - Qatorga chap tugma bilan bosing: keyingi variant, o'ng tugma bilan: oldingisi.
 * - Pastdagi "Size" qatorida [-] va [+] bilan hajmni o'zgartiring.
 */
public class CosmeticsScreen extends Screen {
	private static final int PANEL_W = 250;
	private static final int ROW_H = 22;
	private static final int ROW_GAP = 4;

	private static final int COL_PANEL = 0xEE12121C;
	private static final int COL_BORDER = 0xFF3B3B55;
	private static final int COL_ACCENT = 0xFFD4AF6A;
	private static final int COL_ROW = 0xFF1E1E2D;
	private static final int COL_ROW_HOVER = 0xFF2C2C42;
	private static final int COL_TEXT = 0xFFE8E8F0;
	private static final int COL_MUTED = 0xFF9A9AB0;

	private final Screen parent;
	private final List<CosmeticSlot> slots = new ArrayList<>();

	private int panelX, panelY, panelH;

	public CosmeticsScreen(Screen parent) {
		super(Text.translatable("screen.cosmetics.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		slots.clear();
		for (CosmeticSlot slot : CosmeticSlot.values()) {
			if (!CosmeticRegistry.forSlot(slot).isEmpty()) slots.add(slot);
		}
		panelH = 46 + slots.size() * (ROW_H + ROW_GAP) + 8 + ROW_H + 18 + 20 + 14;
		panelX = (this.width - PANEL_W) / 2;
		panelY = (this.height - panelH) / 2;

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
				.dimensions(panelX + 12, panelY + panelH - 34, PANEL_W - 24, 20).build());
	}

	private int rowY(int index) {
		return panelY + 46 + index * (ROW_H + ROW_GAP);
	}

	private int sizeRowY() {
		return rowY(slots.size()) + 8;
	}

	private static boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	// Hamma maxsus chizish shu yerda: vidjetlar (Done tugmasi) buning ustidan chiziladi.
	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.renderBackground(ctx, mouseX, mouseY, delta);

		// Panel
		ctx.fill(panelX - 1, panelY - 1, panelX + PANEL_W + 1, panelY + panelH + 1, COL_BORDER);
		ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, COL_PANEL);
		ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + 2, COL_ACCENT);

		// Sarlavha
		ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 12, COL_ACCENT);
		ctx.fill(panelX + 12, panelY + 28, panelX + PANEL_W - 12, panelY + 29, COL_BORDER);
		ctx.drawCenteredTextWithShadow(this.textRenderer,
				Text.literal("Click: next  |  Right click: previous"), this.width / 2, panelY + 33, COL_MUTED);

		// Slot qatorlari
		for (int i = 0; i < slots.size(); i++) {
			CosmeticSlot slot = slots.get(i);
			int y = rowY(i);
			int x = panelX + 12;
			int w = PANEL_W - 24;
			boolean hover = inside(mouseX, mouseY, x, y, w, ROW_H);
			ctx.fill(x, y, x + w, y + ROW_H, hover ? COL_ROW_HOVER : COL_ROW);
			ctx.fill(x, y, x + 2, y + ROW_H, hover ? COL_ACCENT : COL_BORDER);

			ctx.drawTextWithShadow(this.textRenderer, slot.displayName(), x + 10, y + 7, COL_MUTED);

			Cosmetic c = CosmeticsConfig.get().equippedIn(slot);
			Text value = c == null ? Text.translatable("cosmetics.none") : c.displayName();
			Text shown = Text.empty().append("< ").append(value).append(" >");
			int tw = this.textRenderer.getWidth(shown);
			ctx.drawTextWithShadow(this.textRenderer, shown, x + w - tw - 8, y + 7, c == null ? COL_MUTED : COL_TEXT);
		}

		// Hajm qatori
		int sy = sizeRowY();
		int sx = panelX + 12;
		int sw = PANEL_W - 24;
		ctx.fill(sx, sy, sx + sw, sy + ROW_H, COL_ROW);
		ctx.fill(sx, sy, sx + 2, sy + ROW_H, COL_BORDER);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("Size"), sx + 10, sy + 7, COL_MUTED);

		int btnW = 20;
		int plusX = sx + sw - 4 - btnW;
		int minusX = plusX - 52 - btnW;
		drawSmallButton(ctx, minusX, sy + 1, btnW, ROW_H - 2, "-", inside(mouseX, mouseY, minusX, sy + 1, btnW, ROW_H - 2));
		drawSmallButton(ctx, plusX, sy + 1, btnW, ROW_H - 2, "+", inside(mouseX, mouseY, plusX, sy + 1, btnW, ROW_H - 2));
		String pct = Math.round(CosmeticsConfig.get().sizeMultiplier * 100) + "%";
		ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(pct), minusX + btnW + 26, sy + 7, COL_TEXT);
	}

	private void drawSmallButton(DrawContext ctx, int x, int y, int w, int h, String label, boolean hover) {
		ctx.fill(x, y, x + w, y + h, hover ? COL_ACCENT : COL_BORDER);
		ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, hover ? COL_ROW_HOVER : COL_PANEL);
		ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(label), x + w / 2, y + (h - 8) / 2, COL_TEXT);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// Slot qatorlari
		for (int i = 0; i < slots.size(); i++) {
			if (inside(mouseX, mouseY, panelX + 12, rowY(i), PANEL_W - 24, ROW_H)) {
				if (button == 0) cycle(slots.get(i), 1);
				else if (button == 1) cycle(slots.get(i), -1);
				return true;
			}
		}
		// Hajm tugmalari
		int sy = sizeRowY();
		int sx = panelX + 12;
		int sw = PANEL_W - 24;
		int btnW = 20;
		int plusX = sx + sw - 4 - btnW;
		int minusX = plusX - 52 - btnW;
		if (button == 0 && inside(mouseX, mouseY, minusX, sy + 1, btnW, ROW_H - 2)) {
			changeSize(-0.1f);
			return true;
		}
		if (button == 0 && inside(mouseX, mouseY, plusX, sy + 1, btnW, ROW_H - 2)) {
			changeSize(0.1f);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private static void changeSize(float delta) {
		CosmeticsConfig cfg = CosmeticsConfig.get();
		float v = Math.round((cfg.sizeMultiplier + delta) * 10f) / 10f;
		cfg.sizeMultiplier = Math.max(0.5f, Math.min(2.0f, v));
		CosmeticsConfig.save();
	}

	private static void cycle(CosmeticSlot slot, int dir) {
		List<Cosmetic> options = new ArrayList<>();
		options.add(null); // "None"
		options.addAll(CosmeticRegistry.forSlot(slot));
		Cosmetic current = CosmeticsConfig.get().equippedIn(slot);
		int next = (options.indexOf(current) + dir + options.size()) % options.size();
		CosmeticsConfig.get().equip(slot, options.get(next));
		CosmeticsConfig.save();
	}

	@Override
	public void close() {
		CosmeticsConfig.save();
		this.client.setScreen(parent);
	}
}
