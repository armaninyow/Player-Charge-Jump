package com.armaninyow.playerchargejump.mixin;

import com.armaninyow.playerchargejump.ChargeJumpState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.21.6_1.21.11
@Mixin(InGameHud.class)
public class InGameHudMixin {

	private static final Identifier JUMP_BAR_BACKGROUND = Identifier.ofVanilla("hud/jump_bar_background");
	private static final Identifier JUMP_BAR_PROGRESS = Identifier.ofVanilla("hud/jump_bar_progress");

	private static final int OVERCHARGE_MIN_PX = 146;
	private static final int BAR_FULL_PX = 182;

	@Shadow @Final private MinecraftClient client;

	@Inject(at = @At("TAIL"), method = "render")
	private void pcj_renderChargeBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		boolean charging = ChargeJumpState.charging;
		boolean lingering = ChargeJumpState.lingerTicks > 0;
		boolean delaying = ChargeJumpState.delaying;
		if (!charging && !lingering && !delaying) return;
		if (client.player == null) return;
		if (client.player.hasVehicle()) return;

		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		int barWidth = BAR_FULL_PX;
		int barHeight = 5;
		int x = (screenWidth - barWidth) / 2;
		int y = screenHeight - 32 + 3;

		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JUMP_BAR_BACKGROUND, x, y, barWidth, barHeight);

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
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JUMP_BAR_PROGRESS, barWidth, barHeight, 0, 0, x, y, fillWidth, barHeight);
		}
	}
}