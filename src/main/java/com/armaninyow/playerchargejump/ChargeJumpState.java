package com.armaninyow.playerchargejump;

public class ChargeJumpState {

	// Set to true when the server sends the opt-in packet on join.
	// The charge jump is disabled entirely on vanilla servers.
	public static boolean serverAllowed = false;

	// How many ticks the jump key has been held
	public static int jumpHeldTicks = 0;

	// Whether we are in the charging phase (past delay, bar visible)
	public static boolean charging = false;

	// Charge progress [0.0, 1.0] — drives bar fill from 0 to 182 px
	public static float chargeProgress = 0.0f;

	// Whether the bar has hit 1.0 and is now in the overcharge shrink phase
	public static boolean overcharged = false;

	// Overcharge shrink progress [0.0, 1.0] — drives bar shrink from 182 down to 146 px
	public static float overchargeProgress = 0.0f;

	public static void reset() {
		jumpHeldTicks = 0;
		charging = false;
		chargeProgress = 0.0f;
		overcharged = false;
		overchargeProgress = 0.0f;
	}
}