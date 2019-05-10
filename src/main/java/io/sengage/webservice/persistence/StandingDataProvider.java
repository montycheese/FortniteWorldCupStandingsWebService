package io.sengage.webservice.persistence;

import java.util.List;

import io.sengage.webservice.model.ListStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.Standing;

public interface StandingDataProvider {
	
	void setStandings(List<Standing> standings);
	ListStandingsResult getStandings(int week, Region region); 
}
