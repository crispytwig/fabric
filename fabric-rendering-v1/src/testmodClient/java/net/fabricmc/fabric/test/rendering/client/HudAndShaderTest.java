/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.rendering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Tests {@link HudRenderCallback} and custom shaders by drawing a green rectangle
 * in the lower-right corner of the screen.
 */
public class HudAndShaderTest implements ClientModInitializer {
	private static final ShaderProgramKey TEST_SHADER = new ShaderProgramKey(
			Identifier.of("fabric-rendering-v1-testmod", "core/test"),
			VertexFormats.POSITION, Defines.EMPTY);

	@Override
	public void onInitializeClient() {
		ShaderProgramKeys.getAll().add(TEST_SHADER);

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			Window window = client.getWindow();
			int x = window.getScaledWidth() - 15;
			int y = window.getScaledHeight() - 15;
			RenderSystem.setShader(TEST_SHADER);
			RenderSystem.setShaderColor(0f, 1f, 0f, 1f);
			Matrix4f positionMatrix = drawContext.getMatrices().peek().getPositionMatrix();
			BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
			buffer.vertex(positionMatrix, x, y, 50);
			buffer.vertex(positionMatrix, x, y + 10, 50);
			buffer.vertex(positionMatrix, x + 10, y + 10, 50);
			buffer.vertex(positionMatrix, x + 10, y, 50);
			BufferRenderer.drawWithGlobalProgram(buffer.end());
			// Reset shader color
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		});
	}
}
