package com.armaninyow.playerchargejump;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerChargeJump implements ModInitializer {
	public static final String MOD_ID = "playerchargejump";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Packet sent from server to client on join to signal the mod is installed server-side
	public static final CustomPacketPayload.Type<OptInPayload> OPT_IN_PACKET_ID =
		new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "opt_in"));

	@Override
	public void onInitialize() {
		// Register the payload type on both sides (required before use)
		PayloadTypeRegistry.clientboundPlay().register(OPT_IN_PACKET_ID, StreamCodec.unit(new OptInPayload()));

		// Send the opt-in packet to every client that joins
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.getPlayer(), new OptInPayload());
		});

		LOGGER.info("PlayerChargeJump loaded!");
	}

	// Empty payload — its mere arrival on the client is the signal
	public record OptInPayload() implements CustomPacketPayload {
		@Override
		public Type<? extends CustomPacketPayload> type() {
			return OPT_IN_PACKET_ID;
		}
	}
}