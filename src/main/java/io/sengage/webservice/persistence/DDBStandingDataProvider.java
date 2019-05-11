package io.sengage.webservice.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import io.sengage.webservice.model.ListStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.Standing;

public class DDBStandingDataProvider implements StandingDataProvider {

	private final DynamoDBMapper mapper;
	
	public DDBStandingDataProvider(DynamoDBMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public void setStandings(List<Standing> standings) {
		List<FailedBatch> failed = mapper.batchWrite(standings, Arrays.asList());
		for (FailedBatch batch: failed) {
			System.out.println("Size: " + batch.getUnprocessedItems().entrySet().size());
			batch.getException().printStackTrace();
			
			
		}
		System.out.println("Failed to write: " + failed.size() + " number of entries. Failed: " + failed);
	}
	
	@Override
	public ListStandingsResult getStandings(int week, Region region) {

		String val = ":val1";
		String keyConditionalExpression = String.format("%s = %s", Standing.WEEK_REGION_ATTR_NAME, val);
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(val, new AttributeValue().withS(getWeekRegion(week, region)));
		
		DynamoDBQueryExpression<Standing> query = new DynamoDBQueryExpression<Standing>()
				.withIndexName(Standing.WEEK_REGION_RANK_INDEX)
				.withScanIndexForward(true)
				.withKeyConditionExpression(keyConditionalExpression)
				.withExpressionAttributeValues(eav)
				.withConsistentRead(false)
				.withLimit(100);
		
	    QueryResultPage<Standing> queryResult = mapper.queryPage(Standing.class, query);
	    
	    List<Standing> standings = queryResult.getResults();
	    
	    boolean solos = true;
	    
	    if (!standings.isEmpty()) {
	    	solos = standings.get(0).isSolos();
	    }
	    
	    return ListStandingsResult.builder()
	    		.entries(standings)
	    		.region(region)
	    		.week(week)
	    		.solos(solos)
	    		.build();
	}
	
	private String getWeekRegion(int week, Region region) {
		return String.format("%d-%s", week, region.name());
	}

	@Override
	public Optional<Standing> getStanding(String weekRegionName) {
		return Optional.ofNullable(mapper.load(Standing.class, weekRegionName));
	}

	@Override
	public Optional<Standing> getStanding(String weekRegion, int rank) {
		String val = ":val1";
		String val2 = ":val2";
		String keyConditionalExpression = String.format("%s = %s and %s = %s", Standing.WEEK_REGION_ATTR_NAME, val, 
				Standing.RANK_ATTR_NAME, val2);
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(val, new AttributeValue().withS(weekRegion));
		eav.put(val2, new AttributeValue().withN(Integer.toString(rank)));
		
		DynamoDBQueryExpression<Standing> query = new DynamoDBQueryExpression<Standing>()
				.withIndexName(Standing.WEEK_REGION_RANK_INDEX)
				.withKeyConditionExpression(keyConditionalExpression)
				.withExpressionAttributeValues(eav)
				.withConsistentRead(false)
				.withLimit(10);
		
	    QueryResultPage<Standing> queryResult = mapper.queryPage(Standing.class, query);
	    
	    List<Standing> standings = queryResult.getResults();
	    
	    if (standings.size() > 1) {
	    	throw new IllegalStateException("More than one standing with a given rank, system is in an inconsistent state: " + standings.toString());
	    }
	    if (standings.isEmpty()) {
	    	return Optional.empty();
	    }
	    return Optional.of(standings.get(0));
	}

	@Override
	public void deleteStanding(Standing standing) {
		mapper.delete(standing);
		
	}

}
