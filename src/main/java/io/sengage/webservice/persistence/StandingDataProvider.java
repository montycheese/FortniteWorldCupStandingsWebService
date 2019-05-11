package io.sengage.webservice.persistence;

import java.util.List;
import java.util.Optional;

import io.sengage.webservice.model.ListStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.Standing;

public interface StandingDataProvider {
	
	void setStandings(List<Standing> standings);
	
	Optional<Standing> getStanding(String weekRegionName);
	
	Optional<Standing> getStanding(String weekRegion, int rank);
	
	ListStandingsResult getStandings(int week, Region region);
	
	void deleteStanding(Standing standing);
}
