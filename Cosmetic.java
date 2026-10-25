package uz.cosmeticsmod;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.function.Function;

/**
 * Bitta cosmetic ta'rifi. Yangi narsa qo'shish uchun {@link CosmeticRegistry} ichida
 * shu klassdan yangi obyekt yaratib, register() qilish kifoya.
 */
public final class Cosmetic {
	public final String id;
	public final CosmeticSlot slot;
	public final Identifier texture;
	private final String meshResource;
	private final Function<PlayerEntityModel, ModelPart> anchor;

	// Model ichidagi "tayanch nuqta" (model birligida, 1/16 blok) - model shu nuqta atrofida joylashadi
	private final float pivotX, pivotY, pivotZ;
	// Anchor (tana/bosh) bo'lagiga nisbatan joylashuv (piksel, 1/16 blok)
	private final float offX, offY, offZ;
	private final float baseScale;
	private final float rotXDeg;

	private QuadMesh mesh;

	public Cosmetic(String id, CosmeticSlot slot, String meshResource, Identifier texture,
					Function<PlayerEntityModel, ModelPart> anchor,
					float pivotX, float pivotY, float pivotZ,
					float offX, float offY, float offZ,
					float baseScale, float rotXDeg) {
		this.id = id;
		this.slot = slot;
		this.meshResource = meshResource;
		this.texture = texture;
		this.anchor = anchor;
		this.pivotX = pivotX;
		this.pivotY = pivotY;
		this.pivotZ = pivotZ;
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		this.baseScale = baseScale;
		this.rotXDeg = rotXDeg;
	}

	public Text displayName() {
		return Text.translatable("cosmetic.cosmetics." + id);
	}

	public QuadMesh mesh() {
		if (mesh == null) mesh = QuadMesh.load(meshResource);
		return mesh;
	}

	/** Matritsani cosmetic joylashadigan joyga o'tkazadi. Chaqiruvchi push/pop qiladi. */
	public void applyTransform(MatrixStack matrices, PlayerEntityModel model, float sizeMultiplier) {
		anchor.apply(model).rotate(matrices); // tana egilsa (sneak), cosmetic ham egiladi
		matrices.translate(offX / 16f, offY / 16f, offZ / 16f);
		float s = baseScale * sizeMultiplier;
		matrices.scale(s, s, s);
		if (rotXDeg != 0f) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotXDeg));
		matrices.translate(-pivotX / 16f, -pivotY / 16f, -pivotZ / 16f);
	}
}
