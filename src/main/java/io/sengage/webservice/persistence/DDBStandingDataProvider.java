package io.sengage.webservice.persistence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return String.format("%d-%s", Integer.toString(week), region.name());
	}

}
