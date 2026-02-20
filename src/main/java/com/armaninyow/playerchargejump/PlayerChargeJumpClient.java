package com.armaninyow.playerchargejump;

import com.armaninyow.playerchargejump.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class PlayerChargeJumpClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModConfig.register();
	}
}