package io.sengage.webservice.model;

import lombok.Getter;

@Getter
public enum Tournament {
	WORLD_CUP_QUALIFIERS("World Cup"),
	CHAMPION_SERIES_QUALIFIERS("Champion Series")
	;
	
	private Tournament(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	private String friendlyName;
	
	public static Tournament from(String type) {
		for (Tournament tournament : values()) {
			if (tournament.name().equalsIgnoreCase(type)) {
				return tournament;
			}
		}
		throw new IllegalArgumentException("Invalid tournament: " + type);
	}
}
