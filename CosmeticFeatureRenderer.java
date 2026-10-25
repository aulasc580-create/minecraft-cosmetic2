package uz.cosmeticsmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

/** O'yinchi modeliga cosmetic'larni chizadi. Faqat O'ZINGIZNING o'yinchingizda ishlaydi. */
public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	public CosmeticFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
					   PlayerEntityRenderState state, float limbAngle, float limbDistance) {
		if (state.invisible) return;
		if (!isLocalPlayer(state)) return;

		CosmeticsConfig config = CosmeticsConfig.get();
		for (Cosmetic cosmetic : config.allEquipped()) {
			matrices.push();
			cosmetic.applyTransform(matrices, getContextModel(), config.sizeMultiplier);
			VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(cosmetic.texture));
			cosmetic.mesh().render(matrices.peek(), consumer, light, OverlayTexture.DEFAULT_UV);
			matrices.pop();
		}
	}

	/** Render holatida o'yinchi obyekti yo'q, shuning uchun joylashuv bo'yicha o'zimizni aniqlaymiz. */
	private static boolean isLocalPlayer(PlayerEntityRenderState state) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity me = mc.player;
		if (me == null) return false;
		float tickDelta = mc.getRenderTickCounter().getTickDelta(false);
		Vec3d pos = me.getLerpedPos(tickDelta);
		double dx = state.x - pos.x;
		double dy = state.y - pos.y;
		double dz = state.z - pos.z;
		return dx * dx + dy * dy + dz * dz < 0.09;
	}
}
