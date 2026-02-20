package com.armaninyow.playerchargejump.mixin;

import com.armaninyow.playerchargejump.ChargeJumpState;
import com.armaninyow.playerchargejump.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ClientPlayerEntityMixin {

	@Unique
	private boolean pcj_wasJumpPressed = false;

	/**
	 * Tick the charge-jump state machine.
	 * Triggered by Shift+Space. Vanilla Space alone is left untouched,
	 * so repeated jumping still works normally when Shift is not held.
	 */
	@Inject(at = @At("HEAD"), method = "tickMovement")
	private void pcj_tickMovement(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		MinecraftClient client = MinecraftClient.getInstance();

		if (!(self instanceof ClientPlayerEntity player)) return;
		if (client.player != player) return;

		PlayerAbilities abilities = player.getAbilities();
		if (abilities.flying || player.hasVehicle()) {
			if (ChargeJumpState.charging) ChargeJumpState.reset();
			pcj_wasJumpPressed = false;
			return;
		}

		// If we left the ground, reset charging state
		if (!player.isOnGround()) {
			if (ChargeJumpState.charging) ChargeJumpState.reset();
			pcj_wasJumpPressed = isChargeComboPressed(client);
			return;
		}

		boolean jumpPressed = isChargeComboPressed(client);
		ModConfig cfg = ModConfig.get();

		if (jumpPressed) {
			if (!pcj_wasJumpPressed) {
				// Combo just went down this tick — start counter
				ChargeJumpState.jumpHeldTicks = 1;
			} else {
				ChargeJumpState.jumpHeldTicks++;
			}

			int delay = cfg.chargeDelay;
			if (ChargeJumpState.jumpHeldTicks > delay) {
				ChargeJumpState.charging = true;

				int ticksCharging = ChargeJumpState.jumpHeldTicks - delay;
				float progress = (float) ticksCharging / cfg.chargeSpeed;

				if (progress >= 1.0f) {
					ChargeJumpState.chargeProgress = 1.0f;
					ChargeJumpState.overcharged = true;
					// Tick the shrink animation: takes chargeSpeed ticks to shrink 182→146
					int ticksOvercharged = ticksCharging - cfg.chargeSpeed;
					ChargeJumpState.overchargeProgress = Math.min(1.0f, (float) ticksOvercharged / cfg.chargeSpeed);
				} else {
					ChargeJumpState.chargeProgress = progress;
				}
			}
		} else if (pcj_wasJumpPressed) {
			// Combo just released this tick
			if (ChargeJumpState.charging) {
				// Compute fill width in pixels — mirrors the HUD renderer logic exactly
				int fillPx;
				if (ChargeJumpState.overcharged) {
					fillPx = 182 - (int) (ChargeJumpState.overchargeProgress * (182 - 146));
				} else {
					fillPx = (int) (ChargeJumpState.chargeProgress * 182);
				}

				// Sweet spot: 161–182 px
				boolean inSweetSpot = fillPx >= 161 && fillPx <= 182;

				if (inSweetSpot) {
					// Charge jump: 24 px = 1.5 blocks = fence clear height.
					// Power-law fit to MC physics: v = 0.36853 * h^0.56522, accurate to <0.4%.
					double blocks = 24 / 16.0;
					double velocity = 0.36853 * Math.pow(blocks, 0.56522);
					player.setVelocity(player.getVelocity().x, velocity, player.getVelocity().z);
				} else {
					// Outside sweet spot — standard vanilla jump
					player.jump();
				}
				ChargeJumpState.reset();
			} else {
				// Released before delay — standard vanilla jump
				ChargeJumpState.jumpHeldTicks = 0;
				if (player.isOnGround()) {
					player.jump();
				}
			}
		}

		pcj_wasJumpPressed = jumpPressed;
	}

	/**
	 * Suppress vanilla jump while Shift+Space is held so we control
	 * the exact moment the player leaves the ground.
	 * Vanilla Space alone (without Shift) is never cancelled here.
	 */
	@Inject(at = @At("HEAD"), method = "jump", cancellable = true)
	private void pcj_jump(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		MinecraftClient client = MinecraftClient.getInstance();

		if (!(self instanceof ClientPlayerEntity player)) return;
		if (client.player != player) return;

		if (isChargeComboPressed(client)) {
			ci.cancel();
		}
	}

	/**
	 * Returns true only when both the jump key (Space) and the sneak key (Shift)
	 * are held simultaneously.
	 */
	@Unique
	private static boolean isChargeComboPressed(MinecraftClient client) {
		return client.options.jumpKey.isPressed() && client.options.sneakKey.isPressed();
	}
}