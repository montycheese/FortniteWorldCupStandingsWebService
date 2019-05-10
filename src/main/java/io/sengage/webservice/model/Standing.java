package io.sengage.webservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder=true)
@Data
@DynamoDBTable(tableName = Standing.TABLE_NAME)
public class Standing {
	public static final String TABLE_NAME = "Standing";
	public static final String WEEK_REGION_RANK_INDEX = "WeekRegion-Rank-Index";
	public static final String WEEK_REGION_ATTR_NAME = "WeekRegion";
	public static final String RANK_ATTR_NAME = "Rank";
	
	private String id;
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = RANK_ATTR_NAME, attributeName = RANK_ATTR_NAME)
	private int rank;
	private int points;
	private int prize;
	private int week;
	private String currencySymbol;
	@DynamoDBTypeConvertedEnum
	private Region region;
	private boolean solos;
	private String name1;
	private String name2;
	@DynamoDBIndexHashKey(globalSecondaryIndexName = WEEK_REGION_RANK_INDEX, attributeName = WEEK_REGION_ATTR_NAME)
	private String weekRegion;
	
}
