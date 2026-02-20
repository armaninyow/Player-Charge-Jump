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

@Mixin(InGameHud.class)
public class InGameHudMixin {

	private static final Identifier JUMP_BAR_BACKGROUND = Identifier.ofVanilla("hud/jump_bar_background");
	private static final Identifier JUMP_BAR_PROGRESS = Identifier.ofVanilla("hud/jump_bar_progress");

	private static final int OVERCHARGE_MIN_PX = 146;
	private static final int BAR_FULL_PX = 182;

	@Shadow @Final private MinecraftClient client;

	@Inject(at = @At("TAIL"), method = "render")
	private void pcj_renderChargeBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (!ChargeJumpState.charging) return;
		if (client.player == null) return;
		if (client.player.hasVehicle()) return;

		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		int barWidth = BAR_FULL_PX;
		int barHeight = 5;
		int x = (screenWidth - barWidth) / 2;
		// Match vanilla renderMountJumpBar Y: scaledHeight - 32 + 3, same row as XP bar
		int y = screenHeight - 32 + 3;

		// Draw background
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JUMP_BAR_BACKGROUND, x, y, barWidth, barHeight);

		// Fill width: grows 0→182 during charge, then shrinks 182→146 during overcharge
		int fillWidth;
		if (ChargeJumpState.overcharged) {
			fillWidth = BAR_FULL_PX - (int) (ChargeJumpState.overchargeProgress * (BAR_FULL_PX - OVERCHARGE_MIN_PX));
		} else {
			fillWidth = (int) (ChargeJumpState.chargeProgress * barWidth);
		}

		if (fillWidth > 0) {
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JUMP_BAR_PROGRESS, barWidth, barHeight, 0, 0, x, y, fillWidth, barHeight);
		}
	}
}