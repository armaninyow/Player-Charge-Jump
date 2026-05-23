package com.armaninyow.playerchargejump.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("playerchargejump.json");

	private static ModConfig INSTANCE = new ModConfig();

	// How many ticks to hold before charging begins (5–20)
	public int chargeDelay = 5;

	// How many ticks to fill the bar fully (10–40)
	public int chargeSpeed = 10;

	public static ModConfig get() {
		return INSTANCE;
	}

	public static void load() {
		if (Files.exists(PATH)) {
			try (Reader r = Files.newBufferedReader(PATH)) {
				INSTANCE = GSON.fromJson(r, ModConfig.class);
			} catch (IOException e) {
				INSTANCE = new ModConfig();
			}
		}
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(PATH)) {
			GSON.toJson(INSTANCE, w);
		} catch (IOException e) {
			// ignore
		}
	}
}