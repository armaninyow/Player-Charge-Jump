package com.armaninyow.playerchargejump.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "playerchargejump")
public class ModConfig implements ConfigData {

	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.BoundedDiscrete(min = 5, max = 20)
	public int chargeDelay = 5;

	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.BoundedDiscrete(min = 10, max = 40)
	public int chargeSpeed = 10;

	public static ModConfig get() {
		return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static void register() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
	}
}
