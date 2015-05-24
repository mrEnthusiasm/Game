package game.player;

public enum StatusType {
	NORMAL, FROZEN;

	public double getAdjustment(StatType st) {
		switch (this) {
		case FROZEN:
			switch (st) {
			case AGILITY:
				return 0.5;
			case MOVEMENT:
				return 0.5;
			default:
				return 1;
			}

		case NORMAL:
			return 1;

		default:
			System.err.println("UNKNOWN PLAYER STATUS TYPE");
			return -1;
		}
	}
}
