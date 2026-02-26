package com.armaninyow.playerchargejump;

import com.armaninyow.playerchargejump.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PlayerChargeJumpClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModConfig.register();

		// Receive the opt-in packet from the server — enable the charge jump
		ClientPlayNetworking.registerGlobalReceiver(PlayerChargeJump.OPT_IN_PACKET_ID, (payload, context) -> {
			ChargeJumpState.serverAllowed = true;
		});

		// Clear the flag when disconnecting so it doesn't carry over to the next session
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ChargeJumpState.serverAllowed = false;
			ChargeJumpState.reset();
		});
	}
}