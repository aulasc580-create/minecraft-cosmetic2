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
 * Har bir qatorda [<] va [>] tugmalari bor: ular oldingi/keyingi variantni tanlaydi.
 * Pastda hajm: [-] va [+].
 */
public class CosmeticsScreen extends Screen {
	private static final int PANEL_W = 340;
	private static final int HEADER_H = 64;
	private static final int ROW_H = 32;
	private static final int ROW_GAP = 8;
	private static final int ARROW_W = 30;
	private static final int PAD = 14;

	private static final int COL_PANEL = 0xF0101018;
	private static final int COL_BORDER = 0xFF3B3B55;
	private static final int COL_ACCENT = 0xFFD4AF6A;
	private static final int COL_ROW = 0xFF1C1C2A;
	private static final int COL_BTN = 0xFF2A2A40;
	private static final int COL_BTN_HOVER = 0xFF44446A;
	private static final int COL_TEXT = 0xFFF2F2FA;
	private static final int COL_MUTED = 0xFF9A9AB4;
	private static final int COL_ACTIVE = 0xFF8FE388;

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
		// sarlavha + slot qatorlari + hajm qatori + Done tugmasi
		panelH = HEADER_H + slots.size() * (ROW_H + ROW_GAP) + 10 + ROW_H + 18 + 24 + PAD;
		panelX = (this.width - PANEL_W) / 2;
		panelY = Math.max(4, (this.height - panelH) / 2);

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> this.close())
				.dimensions(panelX + PAD, panelY + panelH - PAD - 24, PANEL_W - 2 * PAD, 24).build());
	}

	// ---- joylashuv hisob-kitoblari ----
	private int rowX() { return panelX + PAD; }
	private int rowW() { return PANEL_W - 2 * PAD; }
	private int rowY(int i) { return panelY + HEADER_H + i * (ROW_H + ROW_GAP); }
	private int sizeRowY() { return rowY(slots.size()) + 10; }
	/** Qatorning chap o'rtadagi "<" tugmasi x koordinatasi */
	private int prevX() { return rowX() + 110; }
	/** O'ngdagi ">" tugmasi x koordinatasi */
	private int nextX() { return rowX() + rowW() - 4 - ARROW_W; }
	private int btnY(int rowY) { return rowY + 3; }
	private int btnH() { return ROW_H - 6; }

	private static boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && mx < x + w && my >= y && my < y + h;
	}

	// ---- chizish ----
	// Hamma maxsus chizish shu yerda: vidjetlar (Done tugmasi) buning ustidan chiziladi.
	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.renderBackground(ctx, mouseX, mouseY, delta);

		// Panel
		ctx.fill(panelX - 1, panelY - 1, panelX + PANEL_W + 1, panelY + panelH + 1, COL_BORDER);
		ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, COL_PANEL);
		ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + 3, COL_ACCENT);

		// Sarlavha (katta) va yordam matni
		drawText(ctx, this.title, this.width / 2, panelY + 14, 2.0f, COL_ACCENT, true);
		drawText(ctx, Text.literal("Use  <  >  or right click a row to change"), this.width / 2, panelY + 40, 1.0f, COL_MUTED, true);
		ctx.fill(panelX + PAD, panelY + 55, panelX + PANEL_W - PAD, panelY + 56, COL_BORDER);
		drawText(ctx, Text.literal("by Rediska4"), panelX + PANEL_W - PAD - 58, panelY + 8, 0.9f, COL_MUTED, false);

		// Slot qatorlari
		for (int i = 0; i < slots.size(); i++) {
			CosmeticSlot slot = slots.get(i);
			int y = rowY(i);
			ctx.fill(rowX(), y, rowX() + rowW(), y + ROW_H, COL_ROW);

			drawText(ctx, slot.displayName(), rowX() + 12, y + 11, 1.2f, COL_MUTED, false);

			Cosmetic c = CosmeticsConfig.get().equippedIn(slot);
			Text value = c == null ? Text.translatable("cosmetics.none") : c.displayName();
			int mid = (prevX() + ARROW_W + nextX()) / 2;
			drawText(ctx, value, mid, y + 11, 1.2f, c == null ? COL_MUTED : COL_ACTIVE, true);

			drawButton(ctx, prevX(), btnY(y), ARROW_W, btnH(), "<", mouseX, mouseY);
			drawButton(ctx, nextX(), btnY(y), ARROW_W, btnH(), ">", mouseX, mouseY);
		}

		// Hajm qatori
		int sy = sizeRowY();
		ctx.fill(rowX(), sy, rowX() + rowW(), sy + ROW_H, COL_ROW);
		drawText(ctx, Text.literal("Size"), rowX() + 12, sy + 11, 1.2f, COL_MUTED, false);
		String pct = Math.round(CosmeticsConfig.get().sizeMultiplier * 100) + "%";
		int mid = (prevX() + ARROW_W + nextX()) / 2;
		drawText(ctx, Text.literal(pct), mid, sy + 11, 1.2f, COL_TEXT, true);
		drawButton(ctx, prevX(), btnY(sy), ARROW_W, btnH(), "-", mouseX, mouseY);
		drawButton(ctx, nextX(), btnY(sy), ARROW_W, btnH(), "+", mouseX, mouseY);
	}

	private void drawButton(DrawContext ctx, int x, int y, int w, int h, String label, int mx, int my) {
		boolean hover = inside(mx, my, x, y, w, h);
		ctx.fill(x, y, x + w, y + h, hover ? COL_ACCENT : COL_BORDER);
		ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, hover ? COL_BTN_HOVER : COL_BTN);
		drawText(ctx, Text.literal(label), x + w / 2, y + (h - 12) / 2 + 1, 1.5f, COL_TEXT, true);
	}

	/** Kattalashtirilgan matn chizadi. (x,y) - matnning chap-yuqori (yoki markaz-yuqori) nuqtasi. */
	private void drawText(DrawContext ctx, Text text, int x, int y, float scale, int color, boolean centered) {
		float drawX = x;
		if (centered) drawX = x - this.textRenderer.getWidth(text) * scale / 2f;
		ctx.getMatrices().push();
		ctx.getMatrices().translate(drawX, (float) y, 0f);
		ctx.getMatrices().scale(scale, scale, 1f);
		ctx.drawTextWithShadow(this.textRenderer, text, 0, 0, color);
		ctx.getMatrices().pop();
	}

	// ---- bosishlar ----
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// O'ng tugma: qator ustida bosilsa keyingi variantga o'tadi (masalan keyingi shlyapa)
		if (button == 1) {
			for (int i = 0; i < slots.size(); i++) {
				if (inside(mouseX, mouseY, rowX(), rowY(i), rowW(), ROW_H)) {
					cycle(slots.get(i), 1);
					return true;
				}
			}
		}
		if (button == 0) {
			for (int i = 0; i < slots.size(); i++) {
				int y = rowY(i);
				if (inside(mouseX, mouseY, prevX(), btnY(y), ARROW_W, btnH())) {
					cycle(slots.get(i), -1);
					return true;
				}
				if (inside(mouseX, mouseY, nextX(), btnY(y), ARROW_W, btnH())) {
					cycle(slots.get(i), 1);
					return true;
				}
			}
			int sy = sizeRowY();
			if (inside(mouseX, mouseY, prevX(), btnY(sy), ARROW_W, btnH())) {
				changeSize(-0.1f);
				return true;
			}
			if (inside(mouseX, mouseY, nextX(), btnY(sy), ARROW_W, btnH())) {
				changeSize(0.1f);
				return true;
			}
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
