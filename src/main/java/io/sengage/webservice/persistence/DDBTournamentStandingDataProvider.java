package io.sengage.webservice.persistence;

import io.sengage.webservice.model.ListTournamentStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.SquadType;
import io.sengage.webservice.model.Tournament;
import io.sengage.webservice.model.TournamentStanding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DDBTournamentStandingDataProvider implements TournamentStandingDataProvider {
	private final DynamoDBMapper mapper;
	
	public DDBTournamentStandingDataProvider(DynamoDBMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public void setStandings(List<TournamentStanding> standings) {
		List<FailedBatch> failed = mapper.batchWrite(standings, Arrays.asList());
		for (FailedBatch batch: failed) {
			System.out.println("Size: " + batch.getUnprocessedItems().entrySet().size());
			batch.getException().printStackTrace();
			
			
		}
		System.out.println("Failed to write: " + failed.size() + " number of entries. Failed: " + failed);
	}
	
	@Override
	public ListTournamentStandingsResult getStandings(Tournament tournament, int week, Region region) {

		String val = ":val1";
		String keyConditionalExpression = String.format("%s = %s", TournamentStanding.TOURNAMENT_WEEK_REGION_ATTR_NAME, val);
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(val, new AttributeValue().withS(getTournamentWeekRegion(tournament, week, region)));
		
		DynamoDBQueryExpression<TournamentStanding> query = new DynamoDBQueryExpression<TournamentStanding>()
				.withIndexName(TournamentStanding.TOURNAMENT_WEEK_REGION_RANK_INDEX)
				.withScanIndexForward(true)
				.withKeyConditionExpression(keyConditionalExpression)
				.withExpressionAttributeValues(eav)
				.withConsistentRead(false)
				.withLimit(100);
		
	    QueryResultPage<TournamentStanding> queryResult = mapper.queryPage(TournamentStanding.class, query);
	    
	    List<TournamentStanding> standings = queryResult.getResults();
	    
	    SquadType squadType = null;
	    
	    if (!standings.isEmpty()) {
	    	squadType = standings.get(0).getSquadType();
	    }
	    
	    return ListTournamentStandingsResult.builder()
	    		.entries(standings)
	    		.region(region)
	    		.week(week)
	    		.squadType(squadType)
	    		.build();
	}
	
	private String getTournamentWeekRegion(Tournament tournament, int week, Region region) {
		return String.format("%s-%d-%s", tournament.name(), week, region.name());
	}

	@Override
	public Optional<TournamentStanding> getStanding(String weekRegionName) {
		return Optional.ofNullable(mapper.load(TournamentStanding.class, weekRegionName));
	}

	@Override
	public Optional<TournamentStanding> getStanding(String weekRegion, int rank) {
		String val = ":val1";
		String val2 = ":val2";
		String keyConditionalExpression = String.format("%s = %s and %s = %s", TournamentStanding.TOURNAMENT_WEEK_REGION_ATTR_NAME, val, 
				TournamentStanding.RANK_ATTR_NAME, val2);
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(val, new AttributeValue().withS(weekRegion));
		eav.put(val2, new AttributeValue().withN(Integer.toString(rank)));
		
		DynamoDBQueryExpression<TournamentStanding> query = new DynamoDBQueryExpression<TournamentStanding>()
				.withIndexName(TournamentStanding.TOURNAMENT_WEEK_REGION_RANK_INDEX)
				.withKeyConditionExpression(keyConditionalExpression)
				.withExpressionAttributeValues(eav)
				.withConsistentRead(false)
				.withLimit(10);
		
	    QueryResultPage<TournamentStanding> queryResult = mapper.queryPage(TournamentStanding.class, query);
	    
	    List<TournamentStanding> standings = queryResult.getResults();
	    
	    if (standings.size() > 1) {
	    	throw new IllegalStateException("More than one standing with a given rank, system is in an inconsistent state: " + standings.toString());
	    }
	    if (standings.isEmpty()) {
	    	return Optional.empty();
	    }
	    return Optional.of(standings.get(0));
	}

	@Override
	public void deleteStanding(TournamentStanding standing) {
		mapper.delete(standing);
		
	}
}
