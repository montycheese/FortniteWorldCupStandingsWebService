package io.sengage.webservice.model;

public enum SquadType {
	SOLOS,
	DUOS,
	TRIOS,
	SQUADS
	;
	
	public static SquadType from(String type) {
		for (SquadType squadType : values()) {
			if (squadType.name().equalsIgnoreCase(type)) {
				return squadType;
			}
		}
		throw new IllegalArgumentException("Invalid squadtype: " + type);
	}
}
