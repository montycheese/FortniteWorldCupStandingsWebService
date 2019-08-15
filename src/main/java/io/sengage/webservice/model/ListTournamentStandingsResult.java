package io.sengage.webservice.model;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ListTournamentStandingsResult {

	private List<TournamentStanding> entries;
	private int week;
	private SquadType squadType;
	Region region;
	private String title;
	private Tournament tournament;
}
