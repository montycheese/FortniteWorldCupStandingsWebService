package io.sengage.webservice.persistence;

import io.sengage.webservice.model.ListTournamentStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.Tournament;
import io.sengage.webservice.model.TournamentStanding;

import java.util.List;
import java.util.Optional;

public interface TournamentStandingDataProvider {
	
	void setStandings(List<TournamentStanding> standings);
	
	Optional<TournamentStanding> getStanding(String tournamentWeekRegionName);
	
	Optional<TournamentStanding> getStanding(String tournamentWeekRegion, int rank);
	
	ListTournamentStandingsResult getStandings(Tournament tournament, int week, Region region);
	
	void deleteStanding(TournamentStanding standing);
}
