package io.sengage.webservice.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder=true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = Standing.TABLE_NAME)
public class Standing {
	public static final String TABLE_NAME = "Standings";
	public static final String WEEK_REGION_RANK_INDEX = "WeekRegion-Rank-Index";
	public static final String WEEK_REGION_ATTR_NAME = "WeekRegion";
	public static final String RANK_ATTR_NAME = "Rank";
	public static final String WEEK_REGION_NAME_ATTR_NAME = "WeekRegionName";
	
	@DynamoDBHashKey(attributeName = WEEK_REGION_NAME_ATTR_NAME)
	private String weekRegionName;
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = WEEK_REGION_RANK_INDEX, attributeName = RANK_ATTR_NAME)
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
	
	private int delta;
	
}
