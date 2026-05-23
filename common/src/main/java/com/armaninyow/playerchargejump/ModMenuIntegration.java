package com.armaninyow.playerchargejump;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.armaninyow.playerchargejump.config.ModConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("config.playerchargejump.title"))
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("config.playerchargejump.category.general"))
				.option(Option.<Integer>createBuilder()
					.name(Component.translatable("config.playerchargejump.option.chargeDelay"))
					.description(OptionDescription.of(Component.translatable("config.playerchargejump.option.chargeDelay.desc")))
					.binding(5, () -> ModConfig.get().chargeDelay, v -> ModConfig.get().chargeDelay = v)
					.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(5, 20).step(1))
					.build())
				.option(Option.<Integer>createBuilder()
					.name(Component.translatable("config.playerchargejump.option.chargeSpeed"))
					.description(OptionDescription.of(Component.translatable("config.playerchargejump.option.chargeSpeed.desc")))
					.binding(10, () -> ModConfig.get().chargeSpeed, v -> ModConfig.get().chargeSpeed = v)
					.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(10, 40).step(1))
					.build())
				.build())
			.save(ModConfig::save)
			.build()
			.generateScreen(parent);
	}
}