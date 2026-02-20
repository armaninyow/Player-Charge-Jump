package com.armaninyow.playerchargejump;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerChargeJump implements ModInitializer {
	public static final String MOD_ID = "playerchargejump";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("PlayerChargeJump loaded!");
	}
}