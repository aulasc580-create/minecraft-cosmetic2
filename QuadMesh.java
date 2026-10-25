package uz.cosmeticsmod;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Oddiy "quad" ro'yxati. Fayl formati (har qator = 1 quad):
 *   x y z u v  (4 ta uchun)  nx ny nz
 * Koordinatalar 1/16 blok birligida (Blockbench/Cubik kabi).
 */
public final class QuadMesh {
	private static final int FLOATS_PER_QUAD = 4 * 5 + 3;

	private final float[] data;
	private final int quadCount;

	private QuadMesh(float[] data, int quadCount) {
		this.data = data;
		this.quadCount = quadCount;
	}

	public static QuadMesh load(String resourcePath) {
		List<float[]> rows = new ArrayList<>();
		try (InputStream in = QuadMesh.class.getResourceAsStream(resourcePath)) {
			if (in == null) throw new IllegalStateException("Model topilmadi: " + resourcePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				String[] parts = line.split("\\s+");
				if (parts.length != FLOATS_PER_QUAD) continue;
				float[] row = new float[FLOATS_PER_QUAD];
				for (int i = 0; i < FLOATS_PER_QUAD; i++) row[i] = Float.parseFloat(parts[i]);
				rows.add(row);
			}
		} catch (Exception e) {
			throw new RuntimeException("Modelni o'qishda xato: " + resourcePath, e);
		}
		float[] all = new float[rows.size() * FLOATS_PER_QUAD];
		for (int q = 0; q < rows.size(); q++) {
			float[] r = rows.get(q);
			for (int i = 0; i < FLOATS_PER_QUAD; i++) {
				float v = r[i];
				// pozitsiyalarni (x,y,z) 16 ga bo'lib blok birligiga o'tkazamiz
				if (i < 20 && i % 5 < 3) v /= 16f;
				all[q * FLOATS_PER_QUAD + i] = v;
			}
		}
		return new QuadMesh(all, rows.size());
	}

	public void render(MatrixStack.Entry entry, VertexConsumer consumer, int light, int overlay) {
		for (int q = 0; q < quadCount; q++) {
			int o = q * FLOATS_PER_QUAD;
			float nx = data[o + 20], ny = data[o + 21], nz = data[o + 22];
			for (int v = 0; v < 4; v++) {
				int b = o + v * 5;
				consumer.vertex(entry, data[b], data[b + 1], data[b + 2])
						.color(255, 255, 255, 255)
						.texture(data[b + 3], data[b + 4])
						.overlay(overlay)
						.light(light)
						.normal(entry, nx, ny, nz);
			}
		}
	}
}
