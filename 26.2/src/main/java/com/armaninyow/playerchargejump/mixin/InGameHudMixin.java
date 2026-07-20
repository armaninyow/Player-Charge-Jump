package com.armaninyow.playerchargejump.mixin;

import com.armaninyow.playerchargejump.ChargeJumpState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class InGameHudMixin {

	private static final Identifier JUMP_BAR_BACKGROUND = Identifier.withDefaultNamespace("hud/jump_bar_background");
	private static final Identifier JUMP_BAR_PROGRESS   = Identifier.withDefaultNamespace("hud/jump_bar_progress");

	private static final int OVERCHARGE_MIN_PX = 146;
	private static final int BAR_FULL_PX       = 182;

	@Shadow @Final private Minecraft minecraft;

	@Inject(
		at = @At("TAIL"),
		method = "extractRenderState",
		locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void pcj_renderChargeBar(
		DeltaTracker deltaTracker,
		boolean shouldRenderLevel,
		boolean resourcesLoaded,
		CallbackInfo ci,
		ProfilerFiller profiler,
		int xMouse,
		int yMouse,
		GuiGraphicsExtractor context
	) {
		boolean charging  = ChargeJumpState.charging;
		boolean lingering = ChargeJumpState.lingerTicks > 0;
		boolean delaying  = ChargeJumpState.delaying;
		if (!charging && !lingering && !delaying) return;
		if (minecraft.player == null) return;
		if (minecraft.player.isPassenger()) return;

		int screenWidth  = context.guiWidth();
		int screenHeight = context.guiHeight();

		int barWidth  = BAR_FULL_PX;
		int barHeight = 5;
		int x = (screenWidth - barWidth) / 2;
		int y = screenHeight - 32 + 3;

		RenderPipeline pipe = RenderPipelines.GUI_TEXTURED;

		context.blitSprite(pipe, JUMP_BAR_BACKGROUND, x, y, barWidth, barHeight);

		int fillWidth;
		if (charging) {
			if (ChargeJumpState.overcharged) {
				fillWidth = BAR_FULL_PX - (int) (ChargeJumpState.overchargeProgress * (BAR_FULL_PX - OVERCHARGE_MIN_PX));
			} else {
				fillWidth = (int) (ChargeJumpState.chargeProgress * barWidth);
			}
		} else {
			fillWidth = ChargeJumpState.lingerFillPx;
		}

		if (fillWidth > 0) {
			// blitSprite(pipeline, id, totalW, totalH, u, v, x, y, regionW, regionH)
			context.blitSprite(pipe, JUMP_BAR_PROGRESS, barWidth, barHeight, 0, 0, x, y, fillWidth, barHeight);
		}
	}
}