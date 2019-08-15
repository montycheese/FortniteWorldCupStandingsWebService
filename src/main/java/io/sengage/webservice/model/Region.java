package io.sengage.webservice.model;

public enum Region {
	NAEAST,
	NAWEST,
	EUROPE,
	OCEANIA,
	ASIA,
	BRAZIL,
	MIDDLE_EAST
	;
	
	public static Region from(String type) {
		// special case
		if (type != null && type.equalsIgnoreCase("middle east")) {
			return Region.MIDDLE_EAST;
		}
		for (Region region : values()) {
			if (region.name().equalsIgnoreCase(type)) {
				return region;
			}
		}
		throw new IllegalArgumentException("Invalid squadtype: " + type);
	}
}
